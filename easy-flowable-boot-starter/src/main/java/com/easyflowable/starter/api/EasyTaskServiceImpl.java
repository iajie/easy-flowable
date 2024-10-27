package com.easyflowable.starter.api;

import com.easyflowable.core.domain.dto.FlowComment;
import com.easyflowable.core.domain.dto.FlowExecutionHistory;
import com.easyflowable.core.domain.enums.FlowCommentType;
import com.easyflowable.core.domain.enums.FlowExecuteType;
import com.easyflowable.core.domain.interfaces.EasyFlowEntityInterface;
import com.easyflowable.core.domain.params.FlowCancellationParam;
import com.easyflowable.core.domain.params.FlowExecuteParam;
import com.easyflowable.core.exception.EasyFlowableException;
import com.easyflowable.core.service.EasyTaskService;
import com.easyflowable.core.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: {@link com.easyflowable.starter.api}
 * @Date: 2024-10-09-14:29
 * @Description:
 * @Author: MoJie
 */
@Transactional(rollbackFor = Exception.class)
public class EasyTaskServiceImpl implements EasyTaskService {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private EasyFlowEntityInterface entityInterface;

    @Override
    public void executeNextStep(FlowExecuteParam executeParam) {
        String taskId = executeParam.getTaskId();
        if (StringUtils.isBlank(taskId)) {
            throw new EasyFlowableException("任务ID不能为空");
        }
        if (executeParam.getExecuteType() == null) {
            throw new EasyFlowableException("执行类型不能为空");
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new EasyFlowableException("任务不存在或已被处理！");
        }
        // 是否执行回退初始节点
        if (executeParam.isInit()) {
            executeParam.setExecuteType(FlowExecuteType.REVOCATION);
        }
        switch (executeParam.getExecuteType()) {
            case AGREE:
            case RESUBMIT:
                this.complete(task, executeParam);
                break;
            case REJECT:
                this.reject(task, executeParam);
                break;
            case REVOCATION:
                this.revocation(task, executeParam);
                break;
            case REJECT_TO_TASK:
                this.rejectToTask(task, executeParam);
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
        String assignee = param.getAssignee();
        if (StringUtils.isBlank(assignee)) {
            assignee = entityInterface.getUserId();
            param.setAssignee(assignee);
        }
        String assigneeName = param.getAssigneeName();
        if (StringUtils.isBlank(assigneeName)) {
            assigneeName = entityInterface.getUserId();
            param.setAssigneeName(assigneeName);
        }
        // 组装审批信息，并保存
        FlowComment flowComment = new FlowComment();
        flowComment.setTaskId(param.getTaskId());
        flowComment.setAssignee(assignee);
        flowComment.setAssigneeName(assigneeName);
        flowComment.setTaskKey(task.getTaskDefinitionKey());
        flowComment.setProcessInstanceId(task.getProcessInstanceId());
        flowComment.setCommentContent(param.getCommentContent());
        flowComment.setExecuteType(param.getExecuteType().getCode());
        flowComment.setExecuteTypeValue(param.getExecuteType().getDescription());
        flowComment.setFlowCommentType(param.getFlowCommentType().getCode());
        flowComment.setVariables(param.getVariables());
        if (param.isComment()) {
            this.addComment(flowComment);
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
        this.checkFlowable(task.getProcessInstanceId());
        // 增加审批意见
        param.setFlowCommentType(FlowCommentType.APPROVE);
        this.saveFlowComment(task, param);
        // 设置当前任务执行人
        taskService.setAssignee(task.getId(), param.getAssignee());
        // 执行任务
        taskService.complete(task.getId(), param.getVariables());
    }

    /**
     * 撤回到初始节点
     * 发起人撤回申请/驳回到初始节点
     * @param task 任务信息
     * @param param 执行参数
     * @Author: MoJie
     * @Date: 2024-10-09 15:15:37
     */
    private void revocation(Task task, FlowExecuteParam param) {
        this.checkFlowable(task.getProcessInstanceId());
        // 获取所有任务节点
        List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).orderByHistoricTaskInstanceStartTime().asc().list();
        // 发起申请任务节点
        String startTaskKey = taskInstances.get(0).getTaskDefinitionKey();
        // 增加审批意见
        param.setFlowCommentType(FlowCommentType.REVOCATION);
        this.saveFlowComment(task, param);
        // 撤回任务到初始节点
        runtimeService.createChangeActivityStateBuilder()
                // 流程实例
                .processInstanceId(task.getProcessInstanceId())
                // 当前任务节点-->指定任务节点
                .moveActivityIdTo(task.getTaskDefinitionKey(), startTaskKey)
                .changeState();
    }

    /**
     * 驳回任务-回退到上一节点
     * @param task 任务
     * @param param 执行参数
     * @Author: MoJie
     * @Date: 2024-10-09 15:17:30
     */
    private void reject(Task task, FlowExecuteParam param) {
        this.checkFlowable(task.getProcessInstanceId());
        // 添加审批意见
        param.setFlowCommentType(FlowCommentType.REJECT);
        this.saveFlowComment(task, param);
        // 获取上次执行节点
        String upNodeKey = this.getUpNodeKey(task.getId());
        // 驳回到上一节点
        taskService.setAssignee(task.getId(), param.getAssignee());
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), upNodeKey)
                .changeState();
    }

    /**
     * 驳回到指定节点
     * @param task 任务
     * @param param 执行参数
     * @Author: MoJie
     * @Date: 2024-10-09 15:18:12
     */
    private void rejectToTask(Task task, FlowExecuteParam param) {
        this.checkFlowable(task.getProcessInstanceId());
        if (StringUtils.isBlank(param.getRejectToTaskId())) {
            throw new EasyFlowableException("未指定驳回的任务ID，操作失败！");
        }
        param.setFlowCommentType(FlowCommentType.REJECT);
        this.saveFlowComment(task, param);
        taskService.setAssignee(task.getId(), param.getAssignee());
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveActivityIdTo(task.getTaskDefinitionKey(), param.getRejectToTaskId())
                .changeState();
    }

    @Override
    @SneakyThrows
    public void addComment(FlowComment flowComment) {
        // 流程全局线程信息
        Authentication.setAuthenticatedUserId(flowComment.getAssignee());
        // 将审批意见转json存
        String message = new ObjectMapper().writeValueAsString(flowComment);
        taskService.addComment(flowComment.getTaskId(), flowComment.getProcessInstanceId(),
                flowComment.getFlowCommentType(), message);
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
    @SneakyThrows
    public List<FlowExecutionHistory> getFlowExecutionHistoryList(String taskId, String assignee) {
        List<FlowExecutionHistory> list = new ArrayList<>();
        Task flowTask = this.getFlowTask(taskId);
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();
        if (StringUtils.isNotBlank(assignee)) {
            historicActivityInstanceQuery.taskAssignee(assignee);
        }
        // 根据任务
        List<HistoricActivityInstance> historicTaskInstances = historicActivityInstanceQuery
                .processInstanceId(flowTask.getProcessInstanceId())
                .orderByHistoricActivityInstanceStartTime().asc().list();
        if (!historicTaskInstances.isEmpty()) {
            // 获取历史审批材料
            List<Comment> commentList = taskService.getTaskComments(taskId);
            for (HistoricActivityInstance instance : historicTaskInstances) {
                FlowExecutionHistory executionHistory = new FlowExecutionHistory();
                executionHistory.setHistoryId(instance.getId());
                executionHistory.setTaskId(taskId);
                executionHistory.setProcessDefinitionId(flowTask.getProcessDefinitionId());
                executionHistory.setExecutionId(instance.getExecutionId());
                executionHistory.setTaskName(instance.getActivityName());
                executionHistory.setStartTime(instance.getStartTime());
                executionHistory.setEndTime(instance.getEndTime());
                executionHistory.setDuration(instance.getDurationInMillis());
                executionHistory.setAssignee(instance.getAssignee());
                for (Comment comment : commentList) {
                    // 如果任务id相同，则将批注信息追加到历史中
                    if (instance.getTaskId().equals(comment.getTaskId())) {
                        executionHistory.setComment(new ObjectMapper().readValue(comment.getFullMessage(), FlowComment.class));
                    }
                }
                list.add(executionHistory);
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
            flowComment.setExecuteType(FlowExecuteType.CANCELLATION.getCode());
            flowComment.setExecuteTypeValue(FlowExecuteType.CANCELLATION.getDescription());
            flowComment.setFlowCommentType(FlowCommentType.CANCELLATION.getCode());
            this.addComment(flowComment);
            runtimeService.deleteProcessInstance(cancellation.getProcessInstanceId(), cancellation.getCancellationCause());
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
        HistoricTaskInstance instance = list.get(0);
        return instance.getTaskDefinitionKey();
    }
}
