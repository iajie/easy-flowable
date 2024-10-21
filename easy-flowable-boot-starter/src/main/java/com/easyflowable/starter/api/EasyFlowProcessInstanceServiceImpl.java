package com.easyflowable.starter.api;

import cn.hutool.json.JSONUtil;
import com.easyflowable.core.domain.dto.FlowComment;
import com.easyflowable.core.domain.dto.FlowExecutionHistory;
import com.easyflowable.core.domain.dto.FlowProcessInstance;
import com.easyflowable.core.domain.dto.Option;
import com.easyflowable.core.domain.enums.FlowCommentType;
import com.easyflowable.core.domain.enums.FlowExecuteType;
import com.easyflowable.core.domain.interfaces.EasyFlowEntityInterface;
import com.easyflowable.core.domain.params.FlowStartParam;
import com.easyflowable.core.exception.EasyFlowableException;
import com.easyflowable.core.service.EasyFlowProcessInstanceService;
import com.easyflowable.core.utils.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class EasyFlowProcessInstanceServiceImpl implements EasyFlowProcessInstanceService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private EasyFlowEntityInterface entityInterface;

    @Override
    public List<FlowProcessInstance> getFlowInstanceList(String key, boolean isFlow) {
        if (StringUtils.isBlank(key)) {
            throw new EasyFlowableException("标识不能为空！");
        }
        ProcessInstanceQuery instanceQuery = runtimeService.createProcessInstanceQuery()
                .processInstanceTenantId(entityInterface.getTenantId());
        if (isFlow) {
            instanceQuery.processDefinitionKey(key);
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
            list.add(flowProcessInstance);
        }
        return list;
    }

    @Override
    public String startProcessInstanceByKey(FlowStartParam startParam) {
        String flowKey = startParam.getFlowKey();
        if (StringUtils.isBlank(flowKey)) {
            throw new EasyFlowableException("流程启动标识不能为空！");
        }
        String businessKey = startParam.getBusinessKey();
        if (StringUtils.isBlank(businessKey)) {
            throw new EasyFlowableException("流程启动业务主键不能为空！");
        }
        if (StringUtils.isBlank(startParam.getProcessName())) {
            throw new EasyFlowableException("流程名称不能为空！");
        }
        // 获取最新流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(flowKey).latestVersion().processDefinitionTenantId(entityInterface.getTenantId())
                .singleResult();
        if (processDefinition == null) {
            throw new EasyFlowableException("未找到指定的流程【" + flowKey + "】,无法启动流程实例");
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
        variables.put("initiator", startUserId);
        // 设置流程发起人-当前登录人
        Authentication.setAuthenticatedUserId(startUserId);
        // 用流程定义的KEY启动，会自动选择KEY相同的流程定义中最新版本的那个(KEY为模型中的流程唯一标识)
        ProcessInstance instance = runtimeService
                .startProcessInstanceByKeyAndTenantId(flowKey, businessKey, variables, entityInterface.getTenantId());
        // 设置流程名称
        runtimeService.setProcessInstanceName(instance.getProcessInstanceId(), startParam.getProcessName());
        // 更新业务状态-默认发起
        runtimeService.updateBusinessStatus(instance.getProcessInstanceId(), "start");
        // 如果自动跳过开始节点，需要设置第一步执行人为启动流程的人
        boolean skipFirstNode = startParam.isSkipFirstNode();
        if (skipFirstNode) {
            Task task = taskService.createTaskQuery()
                    .active().taskAssignee(startUserId)
                    .processDefinitionKey(flowKey).processDefinitionId(instance.getId())
                    .singleResult();
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
                flowComment.setCommentContent(startUsername + "发起流程申请");
                flowComment.setExecuteType(FlowExecuteType.START.getCode());
                flowComment.setExecuteTypeValue(FlowExecuteType.START.getDescription());
                flowComment.setFlowCommentType(FlowCommentType.APPROVE.getCode());
                // 当前任务添加备注
                taskService.addComment(task.getId(), instance.getProcessInstanceId(), FlowCommentType.NORMAL.getCode(), JSONUtil.toJsonStr(flowComment));
                // 执行下一步
                taskService.complete(task.getId());
            }
        }
        Authentication.setAuthenticatedUserId(null);
        return instance.getId();
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
                FlowExecutionHistory executionHistory = new FlowExecutionHistory();
                executionHistory.setHistoryId(instance.getId());
                executionHistory.setTaskId(instance.getTaskId());
                executionHistory.setProcessDefinitionId(instance.getProcessDefinitionId());
                executionHistory.setExecutionId(instance.getExecutionId());
                executionHistory.setTaskName(instance.getActivityName());
                executionHistory.setStartTime(instance.getStartTime());
                executionHistory.setEndTime(instance.getEndTime());
                executionHistory.setDuration(instance.getDurationInMillis());
                executionHistory.setAssignee(instance.getAssignee());
                for (Comment comment : commentList) {
                    // 如果任务id相同，则将批注信息追加到历史中
                    if (instance.getTaskId().equals(comment.getTaskId())) {
                        executionHistory.setComment(JSONUtil.parse(comment.getFullMessage()).toBean(FlowComment.class));
                    }
                }
                list.add(executionHistory);
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
}
