package online.easyflowable.core.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * @package: {@link online.easyflowable.core.domain.dto}
 * @Date: 2024-10-09-10:33
 * @Description: 流程执行历史
 * @Author: MoJie
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
    private FlowComment comment;

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
    
}
