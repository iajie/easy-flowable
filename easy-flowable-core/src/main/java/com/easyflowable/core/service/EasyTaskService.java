package com.easyflowable.core.service;

import com.easyflowable.core.domain.dto.FlowComment;
import com.easyflowable.core.domain.dto.FlowExecutionHistory;
import com.easyflowable.core.domain.dto.Option;
import com.easyflowable.core.domain.params.FlowCancellationParam;
import com.easyflowable.core.domain.params.FlowExecuteParam;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @package: {@link com.easyflowable.core.service}
 * @Date: 2024-10-09-10:44
 * @Description: 任务接口
 * @Author: MoJie
 */
public interface EasyTaskService {

    /**
     * 流程任务执行下一步
     * @param executeParam {@link FlowExecuteParam}流程执行参数
     * @Author: MoJie
     * @Date: 2024-10-09 10:45:29
     */
    void executeNextStep(FlowExecuteParam executeParam);

    /**
     * 添加审批意见
     * @param flowComment {@link FlowComment}审批信息
     * @Author: MoJie
     * @Date: 2024-10-09 10:49:25
     */
    void addComment(FlowComment flowComment);

    /**
     * 获取流程任务对象
     * @param taskId 任务ID
     * @return {@link Task}
     * @Author: MoJie
     * @Date: 2024-10-09 10:49:49
     */
    Task getFlowTask(String taskId, boolean exception);

    default Task getFlowTask(String taskId) {
        return getFlowTask(taskId, true);
    }

    /**
     * 根据实例ID获取流程执行历史记录
     * @param processInstanceId 实例ID
     * @return {@link List<FlowExecutionHistory>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:50:06
     */
    default List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId) {
        return getFlowExecutionHistoryList(processInstanceId, null);
    }

    /**
     * 根据实例ID和用户ID获取流程用户执行历史记录
     * @param processInstanceId 流程实例ID
     * @param assignee 用户ID
     * @return {@link List<FlowExecutionHistory>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:50:23
     */
    List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId, String assignee);

    /**
     * 获取当前任务节点执行人
     * @param taskId 任务ID
     * @return {@link String}
     * @Author: MoJie
     * @Date: 2024-10-09 10:50:47
     */
    default String getUserTaskExecutor(String taskId) {
        return getUserTaskExecutorList(taskId, true, false).get(0);
    }

    /**
     * 获取当前任务节点执行人(候选人)
     * @param taskId 任务ID
     * @param isMainer 是否为执行人
     * @return {@link List<String>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:51:02
     */
    default List<String> getUserTaskExecutors(String taskId, boolean isMainer) {
        return getUserTaskExecutorList(taskId, false, false);
    }

    /**
     * 获取当前任务节点候选部门
     * @param taskId 任务ID
     * @return {@link List<String>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:51:23
     */
    default List<String> getUserTaskOrganIds(String taskId) {
        return getUserTaskExecutorList(taskId, false, true);
    }

    /**
     * 获取当前任务节点执行人(候选人)
     * @param taskId 任务ID
     * @param isMainer 是否为执行人
     * @param isGroup 是否为候选组
     * @return {@link List<String>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:51:38
     */
    List<String> getUserTaskExecutorList(String taskId, boolean isMainer, boolean isGroup);

    /**
     * 流程作废，可以是批量的
     * @param cancellations {@link FlowCancellationParam} 作废参数
     * @Author: MoJie
     * @Date: 2024-10-09 10:53:21
     */
    default void cancellationProcessInstance(FlowCancellationParam...cancellations) {
        cancellationProcessInstance(Arrays.stream(cancellations).collect(Collectors.toList()));
    }

    /**
     * 流程执行作废
     * @param executeParam {@link FlowExecuteParam} 作废参数
     * @Author: MoJie
     * @Date: 2024-10-09 10:53:21
     */
    default void cancellationProcessInstance(FlowExecuteParam executeParam) {
        FlowCancellationParam cancellationParam = new FlowCancellationParam();
        cancellationParam.setCancellationCause(executeParam.getCommentContent().toString());
        cancellationParam.setTaskId(executeParam.getTaskId());
        cancellationParam.setProcessInstanceId(executeParam.getProcessInstanceId());
        cancellationParam.setAssignee(executeParam.getAssignee());
        cancellationParam.setAssigneeName(executeParam.getAssigneeName());
        cancellationProcessInstance(cancellationParam);
    }

    /**
     * 批量流程作废
     * @param cancellations {@link FlowCancellationParam} 作废参数
     * @Author: MoJie
     * @Date: 2024-10-09 10:54:07
     */
    void cancellationProcessInstance(List<FlowCancellationParam> cancellations);

    /**
     * 根据任务ID获取上一个用户任务节点KEY
     * @param taskId 任务ID
     * @return {@link String}
     * @Author: MoJie
     * @Date: 2024-10-09 10:54:31
     */
    String getUpNodeKey(String taskId);

    /**
     * @param instance 运行实例
     * @param commentList 意见列表
     * @return: {@link FlowExecutionHistory}
     * @Author: MoJie
     * @Date: 2024/10/31 19:39
     * @Description: 获取流程实例
     */
    FlowExecutionHistory getFlowExecutionHistory(HistoricActivityInstance instance, List<Comment> commentList);

    /**
     * @param taskId 任务ID
     * @return: {@link List} {@link Option}
     * @Author: MoJie
     * @Date: 2024/11/9 16:43
     * @Description: 根据任务节点获取下一节点的流程变量和当前节点的拓展参数
     */
    Map<String, Object> nextNodeVariables(String taskId);

}
