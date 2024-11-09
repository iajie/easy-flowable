package com.easyflowable.starter.api;

import com.easyflowable.core.constans.Constants;
import com.easyflowable.core.domain.dto.FlowUserTask;
import com.easyflowable.core.domain.dto.Page;
import com.easyflowable.core.domain.entity.DeploymentProcessDef;
import com.easyflowable.core.domain.entity.EasyModel;
import com.easyflowable.core.exception.EasyFlowableException;
import com.easyflowable.core.service.EasyDeploymentService;
import com.easyflowable.core.service.EasyModelService;
import com.easyflowable.core.utils.StringUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @package: {@link com.easyflowable.starter.api}
 * @Date: 2024-10-09-11:19
 * @Description:
 * @Author: MoJie
 */
public class EasyDeploymentServiceImpl implements EasyDeploymentService {

    @Resource
    private EasyModelService modelService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;

    @Override
    public Page<DeploymentProcessDef> page(int current, int size, DeploymentProcessDef params) {
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery().orderByDeploymentTime().desc();
        if (StringUtils.isNotBlank(params.getKey())) {
            deploymentQuery.deploymentKey(params.getKey());
        }
        if (StringUtils.isNotBlank(params.getName())) {
            deploymentQuery.deploymentNameLike(params.getName());
        }
        if (StringUtils.isNotBlank(params.getModelType())) {
            deploymentQuery.deploymentCategory(params.getModelType());
        }

        Page<DeploymentProcessDef> page = new Page<>();
        page.setTotal(deploymentQuery.count());
        List<DeploymentProcessDef> list = new ArrayList<>();
        for (Deployment deployment : deploymentQuery.listPage((current-1) * size, size)) {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deployment.getId()).latestVersion().singleResult();
            list.add(new DeploymentProcessDef()
                    .setId(deployment.getId())
                    .setName(deployment.getName())
                    .setKey(deployment.getKey())
                    .setDeploymentTime(deployment.getDeploymentTime())
                    .setModelType(deployment.getCategory())
                    .setTenantId(deployment.getTenantId())
                    .setProcessDefinitionId(processDefinition.getId())
                    .setHasStartFormKey(processDefinition.hasStartFormKey())
                    .setSuspensionState(processDefinition.isSuspended())
                    .setVersion(processDefinition.getVersion())
            );
        }
        page.setRecords(list);
        return page;
    }

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
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .name(model.getName())
                .key(model.getKey())
                .category(model.getModelType())
                .tenantId(model.getTenantId())
                .addString(Constants.BPMN20_XML(model.getName()), model.getModelEditorXml());
        if (StringUtils.isNotBlank(model.getPicture())) {
            byte[] decode = Base64.getDecoder().decode(model.getPicture().replace("data:image/png;base64,", ""));
            ByteArrayInputStream flowImageStream = new ByteArrayInputStream(decode);
            deploymentBuilder.addInputStream(Constants.DIAGRAM_PNG(model.getName(), model.getKey()), flowImageStream);
        }
        Deployment deployment = deploymentBuilder.deploy();
        if (deployment == null) {
            throw new EasyFlowableException("流程模型部署异常");
        }
        return deployment.getId();
    }

    @Override
    public void deleteDeployment(String deploymentId, Boolean cascade) {
        // 1.查询流程部署信息
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
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
                .processDefinitionId(processDefinitionId).singleResult();
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
                .deploymentKey(flowKey).orderByDeploymentTime().desc()
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
                .processDefinitionKey(flowKey).deploymentId(deployment.getId()).singleResult();
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

    @Override
    public InputStream getFlowImage(String processDefinitionId) {
        this.checkProcessDefinition(processDefinitionId);
        return repositoryService.getProcessDiagram(processDefinitionId);
    }

    private void checkProcessDefinition(String processDefinitionId) {
        ProcessDefinition result = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        if (result == null) {
            throw new EasyFlowableException("未找到流程定义");
        }
    }

    @Override
    @SneakyThrows
    public String getFlowXml(String processDefinitionId) {
        this.checkProcessDefinition(processDefinitionId);
        InputStream processModel = repositoryService.getProcessModel(processDefinitionId);
        return IOUtils.toString(processModel, StandardCharsets.UTF_8);
    }
}
