package online.easyflowable.core.service;

import online.easyflowable.core.domain.dto.FlowComment;
import online.easyflowable.core.domain.dto.FlowExecutionHistory;
import online.easyflowable.core.domain.params.FlowCancellationParam;
import online.easyflowable.core.domain.params.FlowExecuteParam;
import org.flowable.task.api.Task;

import java.util.Arrays;
import java.util.List;

/**
 * @package: {@link online.easyflowable.core.service}
 * @Date: 2024-10-09-10:44
 * @Description: 任务接口
 * @Author: MoJie
 */
public interface EasyFlowTaskService {

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
    Task getFlowTask(String taskId);

    /**
     * 根据任务ID获取流程执行历史记录
     * @param taskId 任务ID
     * @return {@link List<FlowExecutionHistory>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:50:06
     */
    default List<FlowExecutionHistory> getFlowExecutionHistoryList(String taskId) {
        return getFlowExecutionHistoryList(taskId, null);
    }

    /**
     * 根据任务ID和用户ID获取流程用户执行历史记录
     * @param taskId 任务ID
     * @param assignee 用户ID
     * @return {@link List<FlowExecutionHistory>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:50:23
     */
    List<FlowExecutionHistory> getFlowExecutionHistoryList(String taskId, String assignee);

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
        cancellationProcessInstance(Arrays.stream(cancellations).toList());
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

}
