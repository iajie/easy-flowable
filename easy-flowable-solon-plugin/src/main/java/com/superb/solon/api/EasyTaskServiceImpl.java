package com.superb.solon.api;

import com.superb.core.constans.Constants;
import com.superb.core.constans.EasyFlowableContext;
import com.superb.core.domain.dto.FlowComment;
import com.superb.core.domain.dto.FlowExecutionHistory;
import com.superb.core.domain.dto.Option;
import com.superb.core.domain.entity.EasyFlowableUser;
import com.superb.core.domain.enums.FlowCommentType;
import com.superb.core.domain.params.FlowCancellationParam;
import com.superb.core.domain.params.FlowExecuteParam;
import com.superb.core.exception.EasyFlowableException;
import com.superb.core.service.EasyTaskService;
import com.superb.core.service.EasyUserService;
import com.superb.core.utils.BpmnUtils;
import com.superb.core.utils.EasyFlowableStringUtils;
import lombok.SneakyThrows;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @package: {@link com.superb.starter.api}
 * @Date: 2024-10-09-14:29
 * @Description:
 * @Author: MoJie
 */
@Component
public class EasyTaskServiceImpl implements EasyTaskService {

    @Inject
    private RuntimeService runtimeService;
    @Inject
    private TaskService taskService;
    @Inject
    private HistoryService historyService;
    @Inject
    private RepositoryService repositoryService;
    @Inject
    private EasyUserService userInterface;

    @Override
    public void executeNextStep(FlowExecuteParam executeParam) {
        String taskId = executeParam.getTaskId();
        if (EasyFlowableStringUtils.isBlank(taskId)) {
            throw new EasyFlowableException("任务ID不能为空");
        }
        if (executeParam.getFlowCommentType() == null) {
            throw new EasyFlowableException("执行类型不能为空");
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new EasyFlowableException("任务不存在或已被处理！");
        }
        EasyFlowableUser user = EasyFlowableContext.getUser();
        String assignee = executeParam.getAssignee();
        if (EasyFlowableStringUtils.isBlank(assignee)) {
            executeParam.setAssignee(user.getUserId().toString());
        }
        String assigneeName = executeParam.getAssigneeName();
        if (EasyFlowableStringUtils.isBlank(assigneeName)) {
            executeParam.setAssigneeName(user.getUsername());
        }
        // 1.执行前检查流程
        this.checkFlowable(task.getProcessInstanceId());
        if (executeParam.getFlowCommentType() != FlowCommentType.CANCELLATION &&
                executeParam.getFlowCommentType() != FlowCommentType.AGREE &&
                executeParam.getFlowCommentType() != FlowCommentType.DEL_COMMENT) {
            // 保存流程审批意见
            this.saveFlowComment(task, executeParam);
        }
        // 3.执行(暂存没有操作，就单纯添加审批意见)
        switch (executeParam.getFlowCommentType()) {
            case AGREE:
                this.complete(task, executeParam);
                break;
            case RESUBMIT: // 重新提交回到上次驳回节点
            case REJECT:
                this.reject(task, executeParam);
                break;
            case REBUT:
            case REVOCATION:
                this.revocation(task, executeParam);
                break;
            case REJECT_TO_TASK:
                this.rejectToTask(task, executeParam);
                break;
            case STOP:
                this.taskService.deleteTask(taskId, false);
                break;
            case BEFORE_SIGN:
            case AFTER_SIGN:
                // TODO 前加签/后加签
                break;
            case DELEGATE:
                try {
                    this.taskService.delegateTask(taskId, executeParam.getUserId());
                } catch (Exception e) {
                    this.taskService.deleteComment(executeParam.getCommentId());
                    throw new EasyFlowableException("任务委派异常", e);
                }
                break;
            case ASSIGN:
                try {
                    taskService.setAssignee(task.getId(), executeParam.getUserId());
                } catch (Exception e) {
                    this.taskService.deleteComment(executeParam.getCommentId());
                    throw new EasyFlowableException("任务转办异常", e);
                }
                break;
            case DEL_COMMENT:
                Comment comment = taskService.getComment(executeParam.getCommentId());
                if (comment == null) {
                    throw new EasyFlowableException("评论记录不存在!");
                }
                FlowComment flowComment = EasyFlowableStringUtils.toJava(comment.getFullMessage(), FlowComment.class);
                // 删除附件
                if (EasyFlowableStringUtils.isNotBlank(flowComment.getAttachmentId())) {
                    this.taskService.deleteAttachment(flowComment.getAttachmentId());
                }
                this.taskService.deleteComment(executeParam.getCommentId());
                break;
            case CANCELLATION:
                this.cancellationProcessInstance(executeParam);
                break;
        }
    }

    /**
     * 校验流程状态 
     * @param processInstanceId 流程实例ID
     * @Author: MoJie
     * @Date: 2024-10-09 14:37:20
     */
    private void checkFlowable(String processInstanceId) {
        // 先获取当前任务是否还在运行
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new EasyFlowableException("当前流程不存在或已完成，无法进行操作");
        }
        if (processInstance.isSuspended()) {
            throw new EasyFlowableException("当前流程已终止，需激活流程后操作");
        }
    }

    /**
     * 组装审批意见并保存 
     * @param task 执行任务对象
     * @param param 执行参数
     * @Author: MoJie
     * @Date: 2024-10-09 14:40:28
     */
    private void saveFlowComment(Task task, FlowExecuteParam param) {
        FlowComment flowComment = new FlowComment();
        flowComment.setTaskId(param.getTaskId());
        flowComment.setAssignee(param.getAssignee());
        flowComment.setAssigneeName(param.getAssigneeName());
        flowComment.setProcessInstanceId(task.getProcessInstanceId());
        flowComment.setFlowCommentType(param.getFlowCommentType().getCode());
        flowComment.setVariables(param.getVariables());
        flowComment.setAttachmentId(param.getAttachmentId());
        flowComment.setUserId(param.getUserId());
        Object commentContent = param.getCommentContent();
        if (FlowCommentType.RESUBMIT == param.getFlowCommentType()) {
            // 上次提交信息
            Object upSubmitForm = this.upNodeSubmitForm(task);
            if (upSubmitForm != null) {
                if (commentContent instanceof String) {
                    if (!EasyFlowableStringUtils.isJson((String) commentContent)) {
                        throw new EasyFlowableException("流程表单数据需要JSON字符串或被@EasyItem注解所标记的实体");
                    }
                    flowComment.setExt(commentContent.toString());
                } else if (EasyFlowableStringUtils.isAnnotationEasyItem(commentContent)) {
                    Object upForm = EasyFlowableStringUtils.toJava((String) upSubmitForm, commentContent.getClass());
                    // 获取上次提交信息
                    Map<String, Object> map = EasyFlowableStringUtils.screenTwoProperty(commentContent, upForm);
                    flowComment.setExt(EasyFlowableStringUtils.toJson(map));
                    flowComment.setForm(true);
                } else {
                    flowComment.setExt(EasyFlowableStringUtils.toJson(commentContent));
                }
                flowComment.setCommentContent(param.getAssigneeName() + "重新提交了信息。");
            } else {
                flowComment.setCommentContent(commentContent.toString());
            }
        } else {
            flowComment.setCommentContent(commentContent.toString());
        }
        if (param.isComment()) {
            this.addComment(flowComment);
            param.setCommentId(flowComment.getCommentId());
        }
    }

    /**
     * 执行下一步
     * @param task 任务对象
     * @param param 执行信息
     * @Author: MoJie
     * @Date: 2024-10-09 14:36:46
     */
    private void complete(Task task, FlowExecuteParam param) {
        // 委派人执行任务
        if (task.getDelegationState() != null && task.getDelegationState().name().equals(Constants.PENDING)) {
            if (task.getAssignee().equals(param.getAssignee())) {
                // 保存流程审批意见
                this.saveFlowComment(task, param);
                taskService.resolveTask(task.getId(), param.getVariables());
            } else {
                taskService.deleteComment(param.getCommentId());
                throw new EasyFlowableException("当前任务已被委派，您不是被委派的人，无法执行任务！");
            }
        } else {
            if (EasyFlowableStringUtils.isBlank(task.getAssignee())) {
                throw new EasyFlowableException("当前任务还没有人签收，无法执行！");
            }
            if (!task.getAssignee().equals(param.getAssignee())) {
                throw new EasyFlowableException("您不是节点操作人，无法执行任务！");
            }
            // 保存流程审批意见
            this.saveFlowComment(task, param);
            try {
                // 执行任务
                taskService.complete(task.getId(), param.getVariables());
            } catch (Exception e) {
                taskService.deleteComment(param.getCommentId());
                throw new EasyFlowableException("任务执行异常", e);
            }
        }
    }

    /**
     * 撤回到初始节点
     * 发起人撤回申请/驳回到初始节点
     *
     * @param task         任务信息
     * @param executeParam 执行参数
     * @Author: MoJie
     * @Date: 2024-10-09 15:15:37
     */
    private void revocation(Task task, FlowExecuteParam executeParam) {
        try {
            // 获取所有任务节点
            List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .orderByHistoricTaskInstanceStartTime().asc().list();
            // 发起申请任务节点
            String startTaskKey = taskInstances.get(0).getTaskDefinitionKey();
            // 撤回任务到初始节点
            runtimeService.createChangeActivityStateBuilder()
                    // 流程实例
                    .processInstanceId(task.getProcessInstanceId())
                    // 当前任务节点-->指定任务节点
                    .moveActivityIdTo(task.getTaskDefinitionKey(), startTaskKey)
                    .changeState();
        } catch (Exception e) {
            this.taskService.deleteComment(executeParam.getCommentId());
            throw new EasyFlowableException("任务撤回异常", e);
        }

    }

    /**
     * 驳回任务-回退到上一节点
     *
     * @param task         任务
     * @param executeParam 执行参数
     * @Author: MoJie
     * @Date: 2024-10-09 15:17:30
     */
    private void reject(Task task, FlowExecuteParam executeParam) {
        try {
            // 获取上次执行节点
            String upNodeKey = this.getUpNodeKey(task.getId());
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId())
                    .moveActivityIdTo(task.getTaskDefinitionKey(), upNodeKey)
                    .changeState();
        } catch (Exception e) {
            this.taskService.deleteComment(executeParam.getCommentId());
            throw new EasyFlowableException("驳回任务异常", e);
        }
    }

    /**
     * 驳回到指定节点
     * @param task 任务
     * @param param 执行参数
     * @Author: MoJie
     * @Date: 2024-10-09 15:18:12
     */
    private void rejectToTask(Task task, FlowExecuteParam param) {
        try {
            if (EasyFlowableStringUtils.isBlank(param.getRejectToTaskId())) {
                throw new EasyFlowableException("未指定驳回的任务ID，操作失败！");
            }
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId())
                    .moveActivityIdTo(task.getTaskDefinitionKey(), param.getRejectToTaskId())
                    .changeState();
        } catch (Exception e) {
            this.taskService.deleteComment(param.getCommentId());
            throw new EasyFlowableException("驳回到指定任务异常", e);
        }

    }

    @Override
    @SneakyThrows
    public void addComment(FlowComment flowComment) {
        // 流程全局线程信息
        Authentication.setAuthenticatedUserId(flowComment.getAssignee());
        // 将审批意见转json存
        Comment comment = taskService.addComment(flowComment.getTaskId(), flowComment.getProcessInstanceId(),
                flowComment.getFlowCommentType(), EasyFlowableStringUtils.toJson(flowComment));
        flowComment.setCommentId(comment.getId());
        // 清除线程数据
        Authentication.setAuthenticatedUserId(null);
    }

    /**
     * @param task 任务对象
     * @return: {@link Object}
     * @Author: MoJie
     * @Date: 2024/11/17 12:18
     * @Description: 获取上次提交的表单信息
     */
    private Object upNodeSubmitForm(Task task) {
        // 重新提交的实例
        List<Comment> resubmitComments = this.taskService.getProcessInstanceComments(task.getProcessInstanceId(),
                FlowCommentType.RESUBMIT.getCode());
        if (resubmitComments.size() > 0) {
            // 按时间倒序
            List<Comment> collect = resubmitComments.stream()
                    .sorted(Comparator.comparing(Comment::getTime).reversed())
                    .collect(Collectors.toList());
            // 获取最新的信息
            String fullMessage = collect.get(0).getFullMessage();
            Map<String, Object> map = EasyFlowableStringUtils.toMap(fullMessage);
            if (map.containsKey("ext")) {
                return map.get("ext");
            }
        }
        List<Comment> startCommit = this.taskService.getProcessInstanceComments(task.getProcessInstanceId(), FlowCommentType.START.getCode());
        String fullMessage = startCommit.get(0).getFullMessage();
        Map<String, Object> map = EasyFlowableStringUtils.toMap(fullMessage);
        if (map.containsKey("ext")) {
            return map.get("ext");
        }
        return null;
    }

    @Override
    public Task getFlowTask(String taskId, boolean exception) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null && exception) {
            throw new EasyFlowableException("未查询到任务信息，操作中断！");
        }
        return task;
    }

    @Override
    public List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId, String assignee) {
        List<FlowExecutionHistory> list = new ArrayList<>();
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        if (EasyFlowableStringUtils.isNotBlank(assignee)) {
            historicActivityInstanceQuery.taskAssignee(assignee);
        }
        // 根据任务
        List<HistoricActivityInstance> historicTaskInstances = historicActivityInstanceQuery
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc().list();
        if (!historicTaskInstances.isEmpty()) {
            List<Comment> taskComments = taskService.getProcessInstanceComments(processInstanceId);
            for (HistoricActivityInstance instance : historicTaskInstances) {
                if (!instance.getActivityType().equals(Constants.SEQUENCE_FLOW) && !instance.getActivityType().contains(Constants.GATEWAY)) {
                    list.add(this.getFlowExecutionHistory(instance, taskComments));
                }
            }
        }
        return list;
    }

    @Override
    public List<String> getUserTaskExecutorList(String taskId, boolean isMainer, boolean isGroup) {
        List<String> list = new ArrayList<>();
        Task flowTask = this.getFlowTask(taskId, false);
        if (flowTask != null) {
            if (isMainer) {
                list.add(flowTask.getAssignee());
            } else {
                // 获取候选人信息
                List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
                if (!identityLinks.isEmpty()) {
                    for (IdentityLink identityLink : identityLinks) {
                        // 不为候选都跳过
                        if (!IdentityLinkType.CANDIDATE.equalsIgnoreCase(identityLink.getType())) {
                            continue;
                        }
                        if (isGroup) {
                            // candidate候选人类型 用户id不为空
                            if (EasyFlowableStringUtils.isNotBlank(identityLink.getGroupId()) && !list.contains(identityLink.getGroupId())) {
                                list.add(identityLink.getGroupId());
                            }
                        } else {
                            // candidate候选人类型 用户id不为空
                            if (EasyFlowableStringUtils.isNotBlank(identityLink.getUserId()) && !list.contains(identityLink.getUserId())) {
                                list.add(identityLink.getUserId());
                            }
                        }

                    }
                }
            }
        }
        return list;
    }

    @Override
    public void cancellationProcessInstance(List<FlowCancellationParam> cancellations) {
        if (cancellations.isEmpty()) {
            throw new EasyFlowableException("没有作废的流程实例");
        }
        for (FlowCancellationParam cancellation : cancellations) {
            FlowComment flowComment = new FlowComment();
            flowComment.setAssignee(cancellation.getAssignee());
            flowComment.setAssigneeName(cancellation.getAssigneeName());
            flowComment.setTaskId(cancellation.getTaskId());
            flowComment.setProcessInstanceId(cancellation.getProcessInstanceId());
            flowComment.setCommentContent(cancellation.getCancellationCause());
            flowComment.setFlowCommentType(FlowCommentType.CANCELLATION.getCode());
            this.addComment(flowComment);
            try {
                runtimeService.deleteProcessInstance(cancellation.getProcessInstanceId(), cancellation.getCancellationCause());
            } catch (Exception e) {
                taskService.deleteComment(flowComment.getCommentId());
                throw new EasyFlowableException("流程作废异常", e);
            }
        }
    }

    @Override
    public String getUpNodeKey(String taskId) {
        Task flowTask = this.getFlowTask(taskId);
        // 获取历史任务节点
        List<HistoricActivityInstance> userTask = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(flowTask.getProcessInstanceId())
                .activityType(Constants.USER_TASK)
                .finished() // 已经执行结束的节点
                .orderByHistoricActivityInstanceEndTime().desc() // 按执行结束时间排序
                .list();
        HistoricActivityInstance instance = userTask.get(0);
        return instance.getActivityId();
    }

    @Override
    public FlowExecutionHistory getFlowExecutionHistory(HistoricActivityInstance instance, List<Comment> commentList) {
        FlowExecutionHistory executionHistory = new FlowExecutionHistory();
        executionHistory.setHistoryId(instance.getId());
        executionHistory.setExecutionId(instance.getExecutionId());
        executionHistory.setTaskId(instance.getTaskId());
        executionHistory.setTaskDefKey(instance.getActivityId());
        executionHistory.setProcessDefinitionId(instance.getProcessDefinitionId());
        executionHistory.setExecutionId(instance.getExecutionId());
        executionHistory.setTaskName(instance.getActivityName());
        executionHistory.setStartTime(instance.getStartTime());
        executionHistory.setEndTime(instance.getEndTime());
        executionHistory.setDuration(instance.getDurationInMillis());
        executionHistory.setAssignee(instance.getAssignee());
        if (EasyFlowableStringUtils.isNotBlank(instance.getTaskId())) {
            List<FlowComment> comments = new ArrayList<>();
            for (Comment comment : commentList) {
                if (instance.getTaskId().equals(comment.getTaskId())) {
                    // 将批注信息追加到历史中
                    if (EasyFlowableStringUtils.isNotBlank(comment.getFullMessage())) {
                        FlowComment flowComment = EasyFlowableStringUtils.toJava(comment.getFullMessage(), FlowComment.class);
                        flowComment.setCommentId(comment.getId());
                        flowComment.setCommentTime(comment.getTime());
                        comments.add(flowComment);
                    }
                }
            }
            executionHistory.setComments(comments);
            // 执行人为空，那么获取候选人和组
            if (EasyFlowableStringUtils.isBlank(instance.getAssignee())) {
                executionHistory.setCandidateUsers(this.getUserTaskExecutorList(instance.getTaskId(), false, false));
                executionHistory.setCandidateGroups(this.getUserTaskExecutorList(instance.getTaskId(), false, true));
            }
        }
        return executionHistory;
    }

    @Override
    public Map<String, Object> nextNodeVariables(String taskId) {
        List<Option> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        Task task = this.getFlowTask(taskId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // 当前任务节点
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        if (flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            Map<String, Object> taskAttributes = BpmnUtils.getTaskAttributes(userTask.getAttributes());
            if (taskAttributes.containsKey("actions")) {
                String actions = taskAttributes.get("actions").toString();
                taskAttributes.put("actions", Arrays.asList(actions.split(",")));
            }
            map.put("attributes", taskAttributes);
            List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
            for (SequenceFlow outgoingFlow : outgoingFlows) {
                FlowElement targetFlowElement = outgoingFlow.getTargetFlowElement();
                if (targetFlowElement instanceof UserTask) {
                    UserTask targetUserTask = (UserTask) targetFlowElement;
                    String assignee = targetUserTask.getAssignee();
                    if (assignee.startsWith("${") && assignee.endsWith("}")) {
                        list.add(new Option(targetUserTask.getName() + "(下节点执行人)", assignee));
                    }
                }
            }
        }
        List<SequenceFlow> outgoingFlows = ((FlowNode)flowElement).getOutgoingFlows();
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            FlowElement targetElement = outgoingFlow.getTargetFlowElement();
            if (targetElement instanceof Gateway) {
                Gateway gateway = (Gateway) targetElement;
                List<SequenceFlow> outgoingFlows1 = gateway.getOutgoingFlows();
                if (outgoingFlows1.size() > 1) {
                    for (SequenceFlow sequenceFlow : outgoingFlows1) {
                        FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                        if (EasyFlowableStringUtils.isNotBlank(sequenceFlow.getConditionExpression())) {
                            list.add(new Option(targetFlowElement.getName(), sequenceFlow.getConditionExpression()));
                        }
                        if (targetFlowElement instanceof UserTask) {
                            UserTask userTask = (UserTask) targetFlowElement;
                            String assignee = userTask.getAssignee();
                            if (assignee.startsWith("${") && assignee.endsWith("}")) {
                                list.add(new Option(userTask.getName() + "(下节点执行人)", assignee));
                            }
                        }
                    }
                }
            } else if (targetElement instanceof UserTask) {
                UserTask userTask = (UserTask) targetElement;
                if (EasyFlowableStringUtils.isNotBlank(userTask.getSkipExpression())) {
                    list.add(new Option(userTask.getName() + "(跳过表达式)", userTask.getSkipExpression()));
                }
            } else if (targetElement instanceof ScriptTask) {
                ScriptTask scriptTask = (ScriptTask) targetElement;
                if (EasyFlowableStringUtils.isNotBlank(scriptTask.getSkipExpression())) {
                    list.add(new Option(scriptTask.getName() + "(跳过表达式)", scriptTask.getSkipExpression()));
                }
            } else if (targetElement instanceof ServiceTask) {
                ServiceTask serviceTask = (ServiceTask) targetElement;
                if (EasyFlowableStringUtils.isNotBlank(serviceTask.getSkipExpression())) {
                    list.add(new Option(serviceTask.getName() + "(跳过表达式)", serviceTask.getSkipExpression()));
                }
            }
        }
        map.put("sequenceFlow", list);
        return map;
    }
}
