package com.superb.solon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.superb.core.constans.Constants;
import com.superb.core.constans.EasyFlowableContext;
import com.superb.core.domain.dto.*;
import com.superb.core.domain.entity.EasyFlowableUser;
import com.superb.core.domain.enums.FlowCommentType;
import com.superb.core.domain.params.FlowStartParam;
import com.superb.core.exception.EasyFlowableException;
import com.superb.core.service.EasyProcessInstanceService;
import com.superb.core.service.EasyTaskService;
import com.superb.core.service.EasyUserService;
import com.superb.core.utils.EasyFlowableStringUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.util.IoUtil;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @package: {@link com.superb.starter.api}
 * @Date: 2024-10-09-13:29
 * @Description:
 * @Author: MoJie
 */
@Component
public class EasyProcessInstanceServiceImpl implements EasyProcessInstanceService {

    @Inject
    private RepositoryService repositoryService;
    @Inject
    private RuntimeService runtimeService;
    @Inject
    private TaskService taskService;
    @Inject
    private HistoryService historyService;
    @Inject
    private EasyTaskService easyTaskService;
    @Inject
    private EasyUserService userService;

    @Override
    public List<FlowProcessInstance> getFlowInstanceList(String key, boolean isFlow, boolean isProcessInstance) {
        if (EasyFlowableStringUtils.isBlank(key)) {
            throw new EasyFlowableException((isProcessInstance ? "流程定义ID" : "流程标识") + "不能为空！");
        }
        ProcessInstanceQuery instanceQuery = runtimeService.createProcessInstanceQuery();
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
            List<Task> tasks = this.taskService.createTaskQuery()
                    .processInstanceId(processInstance.getProcessInstanceId())
                    .active().list();
            if (tasks != null) {
                if (tasks.size() > 1) {
                    flowProcessInstance.setTaskIds(tasks.stream().map(Task::getId).collect(Collectors.joining(",")));
                } else {
                    flowProcessInstance.setTaskId(tasks.get(0).getId());
                }
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
        if (EasyFlowableStringUtils.isNotBlank(flowKey)) {
            processDefinitionQuery.processDefinitionKey(flowKey).latestVersion();
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
        ProcessInstanceQuery processInstanceQuery = runtimeService
                .createProcessInstanceQuery().processInstanceBusinessKey(businessKey);
        if (EasyFlowableStringUtils.isNotBlank(flowKey)) {
            processInstanceQuery.processDefinitionKey(flowKey);
        } else {
            processInstanceQuery.processDefinitionId(processDefinitionId);
        }
        ProcessInstance processInstance = processInstanceQuery.singleResult();
        if (processInstance != null) {
            throw new EasyFlowableException("当前业务主键已启动流程，无法再次启动流程!");
        }
        String startUserId = startParam.getStartUserId();
        EasyFlowableUser user = EasyFlowableContext.getUser();
        if (EasyFlowableStringUtils.isBlank(startUserId)) {
            startUserId = user.getUserId();
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
            instance = runtimeService.startProcessInstanceByKey(flowKey, businessKey, variables);
        } else {
            instance = runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variables);
        }

        if (EasyFlowableStringUtils.isNotBlank(startParam.getProcessName())) {
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
                if (EasyFlowableStringUtils.isBlank(startUsername)) {
                    startUsername = user.getUsername();
                }
                FlowComment flowComment = new FlowComment();
                flowComment.setAssignee(startUserId);
                flowComment.setAssigneeName(startUsername);
                flowComment.setTaskId(task.getId());
                flowComment.setProcessInstanceId(task.getProcessInstanceId());
                flowComment.setForm(true);
                flowComment.setExt(EasyFlowableStringUtils.toJson(startParam.getFormData()));
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
        if (EasyFlowableStringUtils.isBlank(startParam.getProcessDefinitionId()) && EasyFlowableStringUtils.isBlank(flowKey)) {
            throw new EasyFlowableException("流程启动标识或流程定义ID不能为空！");
        } else {
            if (EasyFlowableStringUtils.isNotBlank(flowKey)) {
                isKey = true;
            }
        }
        if (EasyFlowableStringUtils.isBlank(startParam.getBusinessKey())) {
            throw new EasyFlowableException("流程启动业务主键不能为空！");
        }
        if (startParam.isStartFormData()) {
            Object formData = startParam.getFormData();
            if (formData == null) {
                throw new EasyFlowableException("流程表单流程不能为空！");
            }
            if (formData instanceof String) {
                if (EasyFlowableStringUtils.isBlank(formData.toString())) {
                    throw new EasyFlowableException("流程表单流程不能为空！");
                }
                if (!EasyFlowableStringUtils.isJson((String) formData)) {
                    throw new EasyFlowableException("流程表单数据需要JSON字符串或被@EasyItem注解所标记的实体");
                }
            } else if (EasyFlowableStringUtils.isAnnotationEasyItem(formData)){
                startParam.setFormData(EasyFlowableStringUtils.screenTwoProperty(formData, null));
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
                .activityType(Constants.USER_TASK)
                .finished() // 已经执行结束的节点
                .orderByHistoricActivityInstanceEndTime().asc() // 按执行结束时间排序
                .list();
        if (userTask.size() > 0) {
            Task task = this.taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
            // 排除网关 存在并行网关 执行会签完成--返回上一节点时，为网关,且不是当前节点
            for (HistoricActivityInstance historicActivityInstance : userTask) {
                if (!checkTaskGateway(historicActivityInstance) &&
                        !task.getTaskDefinitionKey().equals(historicActivityInstance.getActivityId())) {
                    list.add(new Option(historicActivityInstance.getActivityName(), historicActivityInstance.getActivityId()));
                }
            }
        }
        return list.stream().distinct().collect(Collectors.toList());
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
        List<HistoricActivityInstance> userTask = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType(Constants.USER_TASK)
                .finished() // 已经执行结束的节点
                .orderByHistoricActivityInstanceEndTime().desc() // 按执行结束时间排序
                .list();
        HistoricActivityInstance instance = userTask.get(0);
        return instance.getActivityId();
    }

    public List<FlowProcessInstanceHistory> getFlowInstanceHistoryList(String key, boolean isDeployment) {
        // 查询完成历史实例
        HistoricProcessInstanceQuery instanceQuery = historyService.createHistoricProcessInstanceQuery();
        if (isDeployment) {
            instanceQuery.deploymentId(key);
        } else {
            instanceQuery.processDefinitionKey(key);
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

    @Override
    @SneakyThrows
    public Map<String, Object> processDynamics(String processInstanceId, String processDefinitionId) {
        Map<String, Object> map = new HashMap<>();
        InputStream processModel = repositoryService.getProcessModel(processDefinitionId);
        map.put("data", IoUtil.transferToString(processModel, "UTF-8"));
        List<HistoricActivityInstance> list = this.historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).list();
        if (list != null && list.size() > 0) {
            List<String> activeNode = new ArrayList<>();
            List<String> executeNode = new ArrayList<>();
            for (HistoricActivityInstance activityInstance : list) {
                if (activityInstance.getEndTime() != null) {
                    executeNode.add(activityInstance.getActivityId());
                } else {
                    activeNode.add(activityInstance.getActivityId());
                }
            }
            map.put("activeNode", activeNode);
            map.put("executeNode", executeNode);
        }
        return map;
    }

    @Override
    public Map<String, Object> nodeInfo(String nodeId) {
        Map<String, Object> map = new HashMap<>();
        List<HistoricActivityInstance> list = this.historyService.createHistoricActivityInstanceQuery()
                .activityId(nodeId)
                .orderByHistoricActivityInstanceStartTime().desc().list();
        if (list.size() > 0) {
            HistoricActivityInstance activityInstance = list.get(0);
            String startTime = DateFormatUtils.format(activityInstance.getStartTime(), "yyyy-MM-dd HH:mm:ss");
            if (activityInstance.getEndTime() != null) {
                String endTime = DateFormatUtils.format(activityInstance.getEndTime(), "yyyy-MM-dd HH:mm:ss");
                map.put("endTime", endTime);
            }
            map.put("startTime", startTime);
            map.put("duration", activityInstance.getDurationInMillis());
            String assignee = activityInstance.getAssignee();
            List<EasyFlowableUser> users = new ArrayList<>();
            if (activityInstance.getActivityType().equals(Constants.USER_TASK)) {
                if (EasyFlowableStringUtils.isNotBlank(assignee)) {
                    users.add(userService.getCurrentUser(assignee));
                } else {
                    List<String> executors = this.easyTaskService.getUserTaskExecutors(activityInstance.getTaskId(), false);
                    for (String executor : executors) {
                        users.add(userService.getCurrentUser(executor));
                    }
                }
                map.put("users", users);
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> statics() {
        Map<String, Object> map = new HashMap<>();
        HistoricActivityInstanceQuery query = this.historyService
                .createHistoricActivityInstanceQuery()
                .activityType(Constants.USER_TASK);
        EasyFlowableUser user = EasyFlowableContext.getUser();
        // 待办
        map.put("todo", query.unfinished().count());
        // 已办
        map.put("done", query.finished().count());
        // 我的待办
        map.put("meTodo", query.taskAssignee(user.getUserId()).unfinished().count());
        // 我的已办
        map.put("meDone", query.taskAssignee(user.getUserId()).finished().count());
        return map;
    }

    @Override
    public Page<DoneTask> todoTasks(String keywords, int current, int size, Boolean finished, boolean isMe) {
         HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery()
                .activityType(Constants.USER_TASK)
                .orderByHistoricActivityInstanceStartTime().desc();
        if (finished != null) {
            if (finished) {
                query.finished();
            } else {
                query.unfinished();
            }
        }
        if (isMe) {
            if (EasyFlowableStringUtils.isNotBlank(keywords)) {
                query.taskAssignee(keywords);
            } else {
                query.taskAssignee(EasyFlowableContext.getUser().getUserId());
            }
        }
        Page<DoneTask> page = new Page<>();
        page.setTotal(query.count());
        List<HistoricActivityInstance> tasks = query.listPage((current - 1) * size, size);
        List<DoneTask> list = new ArrayList<>();
        for (HistoricActivityInstance activityInstance : tasks) {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(activityInstance.getProcessInstanceId()).singleResult();
            int status = 0;
            if (historicProcessInstance.getEndTime() != null) {
                if (EasyFlowableStringUtils.isNotBlank(historicProcessInstance.getDeleteReason())) {
                    status = 2;
                } else {
                    status = 1;
                }
            }
            DoneTask doneTask = new DoneTask();
            doneTask.setId(activityInstance.getId());
            doneTask.setStatus(status).setEndTime(activityInstance.getEndTime());
            doneTask.setStartUserId(historicProcessInstance.getStartUserId())
                    .setNodeName(activityInstance.getActivityName())
                    .setStartTime(activityInstance.getStartTime())
                    .setAssignee(activityInstance.getAssignee())
                    .setProcessName(historicProcessInstance.getName());
            if (isMe && Boolean.FALSE.equals(finished)) {
                doneTask.setTaskId(activityInstance.getTaskId());
                doneTask.setProcessInstanceId(activityInstance.getProcessInstanceId());
            }
            // 获取历史审批材料
            List<Comment> commentList = taskService.getProcessInstanceComments(activityInstance.getProcessInstanceId());
            FlowExecutionHistory history = easyTaskService.getFlowExecutionHistory(activityInstance, commentList);
            doneTask.setComments(history.getComments());
            list.add(doneTask);
        }
        page.setRecords(list);
        return page;
    }
}
