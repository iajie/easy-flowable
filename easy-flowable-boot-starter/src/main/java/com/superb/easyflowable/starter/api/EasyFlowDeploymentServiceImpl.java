package com.superb.easyflowable.starter.api;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.superb.easyflowable.core.domain.dto.FlowUserTask;
import com.superb.easyflowable.core.domain.entity.ActReDeployment;
import com.superb.easyflowable.core.domain.entity.EasyModel;
import com.superb.easyflowable.core.domain.entity.EasyModelHistory;
import com.superb.easyflowable.core.domain.interfaces.EasyFlowEntityInterface;
import com.superb.easyflowable.core.exception.EasyFlowableException;
import com.superb.easyflowable.core.mapper.EasyFlowDeploymentMapper;
import com.superb.easyflowable.core.service.EasyFlowDeploymentService;
import com.superb.easyflowable.core.service.EasyModelService;
import com.superb.easyflowable.core.utils.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @package: {@link com.superb.easyflowable.starter.api}
 * @Date: 2024-10-09-11:19
 * @Description:
 * @Author: MoJie
 */
@Service
@Transactional
public class EasyFlowDeploymentServiceImpl extends ServiceImpl<EasyFlowDeploymentMapper, ActReDeployment> implements EasyFlowDeploymentService {

    @Autowired
    private EasyFlowEntityInterface entityInterface;
    @Autowired
    private EasyModelService modelService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public String deploymentModel(String modelId) {
        if (StringUtils.isBlank(modelId)) {
            throw new EasyFlowableException("流程模型ID不能为空");
        }
        EasyModel model = modelService.getById(modelId.trim());
        if (model == null) {
            throw new EasyFlowableException("未找到流程模型，无法进行流程部署！");
        }
        if (StringUtils.isBlank(model.getModelEditorXml())) {
            throw new EasyFlowableException("未进行流程设计，无法进行部署！");
        }
        /*
         * 1.调用flowable api发起部署
         * 2.添加模型部署历史
         * 3.更新流程模型发布版本
         */
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .key(model.getKey())
                .category(model.getModelType().toString())
                .tenantId(model.getTenantId())
                .addString("easy-flowable-" + model.getName(), model.getModelEditorXml())
                .deploy();
        if (deployment == null) {
            throw new EasyFlowableException("流程模型部署异常");
        }
        EasyModelHistory modelHistory = new EasyModelHistory();
        modelHistory.setModelId(modelId);
        modelHistory.setModelEditorXml(model.getModelEditorXml());
        modelHistory.setRemarks(model.getRemarks());
        modelHistory.setVersion(model.getPublishVersion());
        modelHistory.setCreateBy(entityInterface.getTenantId());
        modelService.saveHistory(modelHistory);
        modelService.updateChain()
                .setRaw(EasyModel::getPublishVersion, "publish_version + 1")
                .update();
        return deployment.getId();
    }

    @Override
    public void deleteDeployment(String deploymentId, Boolean cascade) {
        // 1.查询流程部署信息
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(deploymentId).deploymentTenantId(entityInterface.getTenantId())
                .singleResult();
        if (deployment == null) {
            throw new EasyFlowableException("未查询到流程定义，无法删除");
        }
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().deploymentId(deploymentId).list();
        if (!list.isEmpty()) {
            throw new EasyFlowableException("当前流程定义中存在运行中的流程实例，无法删除");
        }
        repositoryService.deleteDeployment(deploymentId, cascade);
    }

    @Override
    public String deploymentState(String processDefinitionId) {
        // 1.查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).processDefinitionTenantId(entityInterface.getTenantId())
                .singleResult();
        if (processDefinition == null) {
            throw new EasyFlowableException("未查询到流程定义，无法操作");
        }
        if (processDefinition.isSuspended()) {
            repositoryService.activateProcessDefinitionById(processDefinitionId);
            return "激活成功！";
        }
        repositoryService.suspendProcessDefinitionById(processDefinitionId);
        return "终止成功！";
    }

    /**
     * 根据流程模型KEY获取流程部署信息
     * @param flowKey 流程KEY
     * @return {@link Deployment}
     * @Author: MoJie
     * @Date: 2024-10-09 13:12:09
     */
    private Deployment getDeployment(String flowKey) {
        List<Deployment> list = repositoryService.createDeploymentQuery()
                .deploymentKey(flowKey)
                .deploymentTenantId(entityInterface.getTenantId())
                .orderByDeploymentTime().desc()
                .list();
        if (list.isEmpty()) {
            throw new EasyFlowableException(String.format("当前流程[%s]未部署，操作终止", flowKey));
        }
        // 返回最新部署的结果
        return list.get(0);
    }

    /**
     * 获取任务变量
     * @param attributes 流程变量
     * @return {@link Map}
     * @Author: MoJie
     * @Date: 2024-10-09 13:26:35
     */
    private Map<String, Object> getTaskAttributes(Map<String, List<ExtensionAttribute>> attributes) {
        Map<String, Object> map = new HashMap<>();
        for (String key : attributes.keySet()) {
            map.put(key, attributes.get(key).get(0).getValue());
        }
        return map;
    }

    @Override
    public List<FlowUserTask> getAllFlowUserTask(String flowKey) {
        Deployment deployment = this.getDeployment(flowKey);
        // 获取部署在引擎中流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(flowKey)
                .deploymentId(deployment.getId())
                .processDefinitionTenantId(entityInterface.getTenantId())
                .singleResult();
        if (processDefinition == null) {
            throw new EasyFlowableException("该流程定义不存在，请部署后再试！");
        }
        // 获得流程信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        // 获取用户任务
        List<UserTask> userTaskList = bpmnModel.getMainProcess().findFlowElementsOfType(UserTask.class);
        if (userTaskList.isEmpty()) {
            return new ArrayList<>();
        }
        List<FlowUserTask> list = new ArrayList<>();
        for (UserTask userTask : userTaskList) {
            FlowUserTask flowUserTask = new FlowUserTask();
            flowUserTask.setId(userTask.getId());
            flowUserTask.setName(userTask.getName());
            flowUserTask.setKey(userTask.getFormKey());
            flowUserTask.setAssignee(userTask.getAssignee());
            flowUserTask.setCandidateUsers(userTask.getCandidateUsers());
            flowUserTask.setCandidateGroups(userTask.getCandidateGroups());
            flowUserTask.setFlowCustomProps(this.getTaskAttributes(userTask.getAttributes()));
            list.add(flowUserTask);
        }
        return list;
    }
}
