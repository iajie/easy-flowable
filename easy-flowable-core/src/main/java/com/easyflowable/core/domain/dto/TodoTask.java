package com.easyflowable.core.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @Author: MoJie
 * @Date: 2024-11-17 13:56
 * @Description: 待办
 */
@Data
@Accessors(chain = true)
public class TodoTask {

    private String id;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 流程名称
     */
    private Date startTime;

    /**
     * 流程待办人
     */
    private String assignee;

    /**
     * 任务发起人
     */
    private String startUserId;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 评论
     */
    private List<FlowComment> comments;
}
