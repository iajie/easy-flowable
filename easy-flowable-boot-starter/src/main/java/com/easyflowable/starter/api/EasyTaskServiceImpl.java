package com.easyflowable.starter.api;

import com.easyflowable.core.constans.Constants;
import com.easyflowable.core.domain.dto.FlowComment;
import com.easyflowable.core.domain.dto.FlowExecutionHistory;
import com.easyflowable.core.domain.dto.Option;
import com.easyflowable.core.domain.enums.FlowCommentType;
import com.easyflowable.core.service.EasyUserService;
import com.easyflowable.core.domain.params.FlowCancellationParam;
import com.easyflowable.core.domain.params.FlowExecuteParam;
import com.easyflowable.core.exception.EasyFlowableException;
import com.easyflowable.core.service.EasyTaskService;
import com.easyflowable.core.utils.StringUtils;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: {@link com.easyflowable.starter.api}
 * @Date: 2024-10-09-14:29
 * @Description:
 * @Author: MoJie
 */
public class EasyTaskServiceImpl implements EasyTaskService {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private EasyUserService userInterface;

    @Override
    public void executeNextStep(FlowExecuteParam executeParam) {
        String taskId = executeParam.getTaskId();
        if (StringUtils.isBlank(taskId)) {
            throw new EasyFlowableException("任务ID不能为空");
        }
        if (executeParam.getFlowCommentType() == null) {
            throw new EasyFlowableException("执行类型不能为空");
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new EasyFlowableException("任务不存在或已被处理！");
        }
        String assignee = executeParam.getAssignee();
        if (StringUtils.isBlank(assignee)) {
            assignee = userInterface.getUserId();
            executeParam.setAssignee(assignee);
        }
        String assigneeName = executeParam.getAssigneeName();
        if (StringUtils.isBlank(assigneeName)) {
            assigneeName = userInterface.getUsername();
            executeParam.setAssigneeName(assigneeName);
        }
        // 1.执行前检查流程
        this.checkFlowable(task.getProcessInstanceId());
        if (executeParam.getFlowCommentType() != FlowCommentType.CANCELLATION &&
                executeParam.getFlowCommentType() != FlowCommentType.RESUBMIT &&
                executeParam.getFlowCommentType() != FlowCommentType.AGREE &&
                executeParam.getFlowCommentType() != FlowCommentType.DEL_COMMENT) {
            // 保存流程审批意见
            this.saveFlowComment(task, executeParam);
        }
        // 3.执行(暂存没有操作，就单纯添加审批意见)
        switch (executeParam.getFlowCommentType()) {
            case RESUBMIT:
            case AGREE:
                this.complete(task, executeParam);
                break;
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
                FlowComment flowComment = StringUtils.toJava(comment.getFullMessage(), FlowComment.class);
                // 删除附件
                if (StringUtils.isNotBlank(flowComment.getAttachmentId())) {
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
        flowComment.setCommentContent(param.getCommentContent());
        flowComment.setFlowCommentType(param.getFlowCommentType().getCode());
        flowComment.setVariables(param.getVariables());
        flowComment.setAttachmentId(param.getAttachmentId());
        flowComment.setUserId(param.getUserId());
        if (param.isForm()) {
            flowComment.setExt(param.getCommentContent());
            flowComment.setForm(true);
            flowComment.setCommentContent(param.getAssigneeName() + "重新提交了信息。");
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
            if (StringUtils.isBlank(task.getAssignee())) {
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
            List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).orderByHistoricTaskInstanceStartTime().asc().list();
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
            if (StringUtils.isBlank(param.getRejectToTaskId())) {
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
                flowComment.getFlowCommentType(), StringUtils.toJson(flowComment));
        flowComment.setCommentId(comment.getId());
        // 清除线程数据
        Authentication.setAuthenticatedUserId(null);
    }

    @Override
    public Task getFlowTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new EasyFlowableException("未查询到任务信息，操作中断！");
        }
        return task;
    }

    @Override
    public List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId, String assignee) {
        List<FlowExecutionHistory> list = new ArrayList<>();
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        if (StringUtils.isNotBlank(assignee)) {
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
        Task flowTask = this.getFlowTask(taskId);
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
                        if (StringUtils.isNotBlank(identityLink.getGroupId()) && !list.contains(identityLink.getGroupId())) {
                            list.add(identityLink.getGroupId());
                        }
                    } else {
                        // candidate候选人类型 用户id不为空
                        if (StringUtils.isNotBlank(identityLink.getUserId()) && !list.contains(identityLink.getUserId())) {
                            list.add(identityLink.getUserId());
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
        // 获取历史任务节点
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .finished()
                .orderByHistoricTaskInstanceEndTime().desc()
                .list();
        HistoricTaskInstance instance = list.get(1);
        return instance.getTaskDefinitionKey();
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
        if (StringUtils.isNotBlank(instance.getTaskId())) {
            List<FlowComment> comments = new ArrayList<>();
            for (Comment comment : commentList) {
                if (instance.getTaskId().equals(comment.getTaskId())) {
                    // 将批注信息追加到历史中
                    if (StringUtils.isNotBlank(comment.getFullMessage())) {
                        FlowComment flowComment = StringUtils.toJava(comment.getFullMessage(), FlowComment.class);
                        flowComment.setCommentId(comment.getId());
                        flowComment.setCommentTime(comment.getTime());
                        comments.add(flowComment);
                    }
                }
            }
            executionHistory.setComments(comments);
            // 执行人为空，那么获取候选人和组
            if (StringUtils.isBlank(instance.getAssignee())) {
                executionHistory.setCandidateUsers(this.getUserTaskExecutorList(instance.getTaskId(), false, false));
                executionHistory.setCandidateGroups(this.getUserTaskExecutorList(instance.getTaskId(), false, true));
            }
        }
        return executionHistory;
    }

    @Override
    public List<Option> nextNodeVariables(String taskId) {
        List<Option> list = new ArrayList<>();
        Task task = this.getFlowTask(taskId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // 当前任务节点
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        List<SequenceFlow> outgoingFlows = ((FlowNode)flowElement).getOutgoingFlows();
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            FlowElement targetElement = outgoingFlow.getTargetFlowElement();
            if (targetElement instanceof ExclusiveGateway) {
                ExclusiveGateway gateway = (ExclusiveGateway) targetElement;
                List<SequenceFlow> outgoingFlows1 = gateway.getOutgoingFlows();
                if (outgoingFlows1.size() > 1) {
                    for (SequenceFlow sequenceFlow : outgoingFlows1) {
                        FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                        if (StringUtils.isNotBlank(sequenceFlow.getSkipExpression())) {
                            list.add(new Option(targetFlowElement.getName() + "(跳过表达式)", sequenceFlow.getSkipExpression()));
                        }
                        if (StringUtils.isNotBlank(sequenceFlow.getConditionExpression())) {
                            list.add(new Option(targetFlowElement.getName(), sequenceFlow.getConditionExpression()));
                        }
                    }
                }
            } else if (targetElement instanceof UserTask) {
                UserTask userTask = (UserTask) targetElement;
                if (StringUtils.isNotBlank(userTask.getSkipExpression())) {
                    list.add(new Option(userTask.getName(), userTask.getSkipExpression()));
                }
            } else if (targetElement instanceof ScriptTask) {
                ScriptTask scriptTask = (ScriptTask) targetElement;
                if (StringUtils.isNotBlank(scriptTask.getSkipExpression())) {
                    list.add(new Option(scriptTask.getName(), scriptTask.getSkipExpression()));
                }
            } else if (targetElement instanceof ServiceTask) {
                ServiceTask serviceTask = (ServiceTask) targetElement;
                if (StringUtils.isNotBlank(serviceTask.getSkipExpression())) {
                    list.add(new Option(serviceTask.getName(), serviceTask.getSkipExpression()));
                }
            }
        }
        return list;
    }
}
