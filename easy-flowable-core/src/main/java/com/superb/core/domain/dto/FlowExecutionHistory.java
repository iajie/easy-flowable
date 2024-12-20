package com.superb.core.domain.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 流程执行历史
 * @since 1.0  2024-10-09-10:33
 * @author MoJie
 */
@Data
public class FlowExecutionHistory {

    /**
     * 历史ID
     */
    private String historyId;

    /**
     * 任务编号
     */
    private String taskId;

    /**
     * 任务执行编号
     */
    private String executionId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务Key
     */
    private String taskDefKey;

    /**
     * 流程部署ID
     */
    private String processDefinitionId;

    /**
     * 流程实例ID
     */
    private String procInsId;

    /**
     * 任务耗时(毫秒)
     */
    private Long duration;

    /**
     * 任务意见
     */
    private List<FlowComment> comments;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务完成时间
     */
    private Date endTime;

    /**
     * 节点执行人
     */
    private String assignee;

    /**
     * 节点候选人
     */
    private List<String> candidateUsers;

    /**
     * 节点候选组
     */
    private List<String> candidateGroups;

}
