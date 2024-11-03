package com.easyflowable.starter.api;

import com.easyflowable.core.constans.Constants;
import com.easyflowable.core.domain.dto.*;
import com.easyflowable.core.domain.enums.FlowCommentType;
import com.easyflowable.core.domain.enums.FlowExecuteType;
import com.easyflowable.core.domain.interfaces.EasyFlowEntityInterface;
import com.easyflowable.core.domain.params.FlowStartParam;
import com.easyflowable.core.exception.EasyFlowableException;
import com.easyflowable.core.service.EasyProcessInstanceService;
import com.easyflowable.core.service.EasyTaskService;
import com.easyflowable.core.utils.CommentUtils;
import com.easyflowable.core.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @package: {@link com.easyflowable.starter.api}
 * @Date: 2024-10-09-13:29
 * @Description:
 * @Author: MoJie
 */
@Transactional(rollbackFor = Exception.class)
public class EasyProcessInstanceServiceImpl implements EasyProcessInstanceService {

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private EasyFlowEntityInterface entityInterface;
    @Resource
    private EasyTaskService easyTaskService;

    @Override
    public List<FlowProcessInstance> getFlowInstanceList(String key, boolean isFlow, boolean isProcessInstance) {
        if (StringUtils.isBlank(key)) {
            throw new EasyFlowableException((isProcessInstance ? "流程定义ID" : "流程标识") + "不能为空！");
        }
        ProcessInstanceQuery instanceQuery = runtimeService.createProcessInstanceQuery()
                .processInstanceTenantId(entityInterface.getTenantId());
        if (isFlow) {
            if (isProcessInstance) {
                instanceQuery.processDefinitionId(key);
            } else {
                instanceQuery.processDefinitionKey(key);
            }
        } else {
            instanceQuery.processInstanceBusinessKey(key);
        }
        // 运行实例列表
        List<ProcessInstance> processInstances = instanceQuery.orderByStartTime().desc().list();
        if (processInstances.isEmpty()) {
            return new ArrayList<>();
        }
        List<FlowProcessInstance> list = new ArrayList<>();
        for (ProcessInstance processInstance : processInstances) {
            FlowProcessInstance flowProcessInstance = new FlowProcessInstance();
            flowProcessInstance.setProcessInstanceId(processInstance.getProcessInstanceId());
            flowProcessInstance.setProcessDefinitionId(processInstance.getProcessDefinitionId());
            flowProcessInstance.setStartUserId(processInstance.getStartUserId());
            flowProcessInstance.setBusinessKey(processInstance.getBusinessKey());
            flowProcessInstance.setName(processInstance.getName());
            flowProcessInstance.setTenantId(processInstance.getTenantId());
            flowProcessInstance.setBusinessKeyStatus(processInstance.getBusinessStatus());
            flowProcessInstance.setDeploymentId(processInstance.getDeploymentId());
            flowProcessInstance.setProcessInstanceVersion(processInstance.getProcessDefinitionVersion());
            flowProcessInstance.setStatus(processInstance.isSuspended());
            Task task = this.taskService.createTaskQuery()
                    .processInstanceId(processInstance.getProcessInstanceId())
                    .active().singleResult();
            if (task != null) {
                flowProcessInstance.setTaskId(task.getId());
            }
            list.add(flowProcessInstance);
        }
        return list;
    }

    @Override
    @SneakyThrows
    public String startProcessInstanceByKey(FlowStartParam startParam) {
        boolean isKey = this.checkStartParam(startParam);
        String businessKey = startParam.getBusinessKey();
        String flowKey = startParam.getFlowKey();
        String processDefinitionId = startParam.getProcessDefinitionId();
        // 获取最新流程定义
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isBlank(flowKey)) {
            processDefinitionQuery.processDefinitionKey(flowKey).latestVersion().processDefinitionTenantId(entityInterface.getTenantId());
        } else {
            processDefinitionQuery.processDefinitionId(processDefinitionId);
        }
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();
        if (processDefinition == null) {
            throw new EasyFlowableException("未找到指定的流程【" + (isKey ? flowKey : processDefinitionId) + "】,无法启动流程实例");
        }
        if (processDefinition.isSuspended()) {
            throw new EasyFlowableException("当前流程【" + processDefinition.getName() + "】已终止，无法启动流程实例");
        }
        // 获取当前业务住建是否已存在运行实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        if (processInstance != null) {
            throw new EasyFlowableException("当前业务主键已启动流程，无法再次启动流程!");
        }
        String startUserId = startParam.getStartUserId();
        if (StringUtils.isBlank(startUserId)) {
            startUserId = entityInterface.getUserId();
        }
        // 启动流程变量(全局)
        Map<String, Object> variables = startParam.getVariables();
        if (variables == null || variables.isEmpty()) {
            variables = new HashMap<>();
        }
        variables.put(Constants.INITIATOR, startUserId);
        // 设置流程发起人-当前登录人
        Authentication.setAuthenticatedUserId(startUserId);
        ProcessInstance instance;
        if (isKey) {
            // 用流程定义的KEY启动，会自动选择KEY相同的流程定义中最新版本的那个(KEY为模型中的流程唯一标识)
            instance = runtimeService
                    .startProcessInstanceByKeyAndTenantId(flowKey, businessKey, variables, entityInterface.getTenantId());
        } else {
            instance = runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variables);
        }

        if (StringUtils.isNotBlank(startParam.getProcessName())) {
            // 设置流程名称
            runtimeService.setProcessInstanceName(instance.getProcessInstanceId(), startParam.getProcessName());
        }
        // 更新业务状态-默认发起
        runtimeService.updateBusinessStatus(instance.getProcessInstanceId(), "start");
        // 如果自动跳过开始节点，需要设置第一步执行人为启动流程的人
        boolean skipFirstNode = startParam.isSkipFirstNode();
        if (skipFirstNode) {
            Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId()).singleResult();
            // 执行第一个节点任务
            if (task != null) {
                // 获取名称
                String startUsername = startParam.getStartUsername();
                if (StringUtils.isBlank(startUsername)) {
                    startUsername = entityInterface.getUsername();
                }
                FlowComment flowComment = new FlowComment();
                flowComment.setAssignee(startUserId);
                flowComment.setAssigneeName(startUsername);
                flowComment.setTaskId(task.getId());
                flowComment.setProcessInstanceId(task.getProcessInstanceId());
                flowComment.setExt(startParam.getFormData());
                flowComment.setCommentContent(startUsername + "发起流程申请");
                flowComment.setFlowCommentType(FlowCommentType.START.getCode());
                // 当前任务添加备注
                taskService.addComment(task.getId(), instance.getProcessInstanceId(),
                        FlowCommentType.START.getCode(), new ObjectMapper().writeValueAsString(flowComment));
                // 执行下一步
                taskService.complete(task.getId(), variables);
            }
        }
        Authentication.setAuthenticatedUserId(null);
        return instance.getId();
    }

    /***
     * @param startParam 启动参数
     * @Return: {@link boolean}
     * @Author: MoJie
     * @Date: 2024/10/28 21:10
     * @Description: 校验启动参数
     */
    private boolean checkStartParam(FlowStartParam startParam) {
        String flowKey = startParam.getFlowKey();
        boolean isKey = false;
        if (StringUtils.isBlank(startParam.getProcessDefinitionId()) || StringUtils.isBlank(flowKey)) {
            throw new EasyFlowableException("流程启动标识或流程定义ID不能为空！");
        } else {
            if (StringUtils.isNotBlank(flowKey)) {
                isKey = true;
            }
        }
        if (StringUtils.isBlank(startParam.getBusinessKey())) {
            throw new EasyFlowableException("流程启动业务主键不能为空！");
        }
        if (startParam.isStartFormData()) {
            if (StringUtils.isBlank(startParam.getFormData())) {
                throw new EasyFlowableException("流程表单流程不能为空！");
            }
            if (!StringUtils.isJson(startParam.getFormData())) {
                throw new EasyFlowableException("流程表单数据需要JSON字符串");
            }
        }
        return isKey;
    }

    @Override
    public void updateProcessInstanceBusinessStatus(String processInstanceId, String status) {
        runtimeService.updateBusinessStatus(processInstanceId, status);
    }

    private boolean checkTaskGateway(HistoricActivityInstance historicActivityInstance){
        boolean isGateway = false;
        Process process = repositoryService.getBpmnModel(historicActivityInstance.getProcessDefinitionId()).getMainProcess();
        FlowNode flowNode = (FlowNode) process.getFlowElement(historicActivityInstance.getActivityId());
        //1. 判断该节点上一个节点是不是并行网关节点
        List<SequenceFlow> incomingFlows = flowNode.getIncomingFlows();
        if (!incomingFlows.isEmpty()) {
            for (SequenceFlow sequenceFlow : incomingFlows) {
                FlowElement upNode = sequenceFlow.getSourceFlowElement();
                if (upNode instanceof Gateway) {
                    isGateway = true;
                }
            }
        }
        return isGateway;
    }

    @Override
    public List<Option> getFlowBackUserTasks(String processInstanceId) {
        List<Option> list = new ArrayList<>();
        List<HistoricActivityInstance> userTask = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .finished() // 已经执行结束的节点
                .orderByHistoricActivityInstanceEndTime().asc() // 按执行结束时间排序
                .list();
        if (userTask.size() > 0) {
            // 排除网关 存在并行网关 执行会签完成--返回上一节点时，为网关
            for (HistoricActivityInstance historicActivityInstance : userTask) {
                if (!checkTaskGateway(historicActivityInstance)) {
                    list.add(new Option(historicActivityInstance.getActivityId(), historicActivityInstance.getActivityName()));
                }
            }
        }
        return list;
    }

    @Override
    public List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId) {
        List<FlowExecutionHistory> list = new ArrayList<>();
        // 根据流程实例ID获取流程历史信息
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        if (!activityInstanceList.isEmpty()) {
            // 获取历史审批材料
            List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
            for (HistoricActivityInstance instance : activityInstanceList) {
                if (!instance.getActivityType().equals(Constants.SEQUENCE_FLOW) &&
                        !instance.getActivityType().contains(Constants.EVENT) &&
                        !instance.getActivityType().contains(Constants.GATEWAY)) {
                    list.add(easyTaskService.getFlowExecutionHistory(instance, commentList));
                }
            }
        }
        return list;
    }

    @Override
    public boolean updateProcessInstanceState(String processInstanceId) {
        // 获得实例信息
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new EasyFlowableException("流程实例不存在或结束！");
        }
        try {
            if (processInstance.isSuspended()) {
                runtimeService.activateProcessInstanceById(processInstanceId);
            } else {
                runtimeService.suspendProcessInstanceById(processInstanceId);
            }
            return true;
        } catch (Exception e) {
            throw new EasyFlowableException(e);
        }
    }

    @Override
    public String getUpNodeKey(String processInstanceId) {
        // 获取历史任务节点
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .orderByHistoricTaskInstanceEndTime().desc()
                .list();
        HistoricTaskInstance instance = list.get(0);
        return instance.getTaskDefinitionKey();
    }

    public List<FlowProcessInstanceHistory> getFlowInstanceHistoryList(String key, boolean isDeployment) {
        // 查询完成历史实例
        HistoricProcessInstanceQuery instanceQuery = historyService.createHistoricProcessInstanceQuery();
        if (isDeployment) {
            instanceQuery.deploymentId(key);
        } else {
            instanceQuery.processDefinitionKey(key).processInstanceTenantId(entityInterface.getTenantId());
        }
        List<FlowProcessInstanceHistory> list = new ArrayList<>();
        List<HistoricProcessInstance> instances = instanceQuery.finished().list();
        for (HistoricProcessInstance processInstance : instances) {
            FlowProcessInstanceHistory flowProcessInstance = new FlowProcessInstanceHistory();
            flowProcessInstance.setProcessInstanceId(processInstance.getId());
            flowProcessInstance.setProcessDefinitionId(processInstance.getProcessDefinitionId());
            flowProcessInstance.setStartUserId(processInstance.getStartUserId());
            flowProcessInstance.setBusinessKey(processInstance.getBusinessKey());
            flowProcessInstance.setName(processInstance.getName());
            flowProcessInstance.setBusinessKeyStatus(processInstance.getBusinessStatus());
            flowProcessInstance.setDeploymentId(processInstance.getDeploymentId());
            flowProcessInstance.setProcessInstanceVersion(processInstance.getProcessDefinitionVersion());
            flowProcessInstance.setStartTime(processInstance.getStartTime());
            flowProcessInstance.setEndTime(processInstance.getEndTime());
            flowProcessInstance.setDuration(processInstance.getDurationInMillis());
            flowProcessInstance.setCancellationCause(processInstance.getDeleteReason());
            list.add(flowProcessInstance);
        }
        return list;
    }
}
