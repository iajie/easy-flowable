package com.easyflowable.core.utils;

import com.easyflowable.core.constans.Constants;
import com.easyflowable.core.domain.dto.FlowComment;
import com.easyflowable.core.domain.dto.FlowExecutionHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.task.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: MoJie
 * @Date: 2024-10-28 19:48
 * @Description: 组装审批意见
 */
public class CommentUtils {

    /**
     * @param instance 运行实例
     * @param commentList 意见列表
     * @Return: {@link FlowExecutionHistory}
     * @Author: MoJie
     * @Date: 2024/10/28 19:50
     * @Description: 组装审批意见
     */
    @SneakyThrows
    public static FlowExecutionHistory getFlowExecutionHistory(HistoricActivityInstance instance, List<Comment> commentList, RuntimeService runtimeService) {
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
                if (comment.getTaskId().equals(instance.getTaskId())) {
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
        }
        return executionHistory;
    }

}
