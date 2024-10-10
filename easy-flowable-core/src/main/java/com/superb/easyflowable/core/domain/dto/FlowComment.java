package com.superb.easyflowable.core.domain.dto;

import lombok.Data;

import java.util.Map;

/**
 * @package: {@link com.superb.easyflowable.core.domain.dto}
 * @Date: 2024-10-09-10:35
 * @Description: 任务执行意见
 * @Author: MoJie
 */
@Data
public class FlowComment {

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 任务ID（缺省）
     */
    private String taskId;

    /**
     * 任务定义KEY
     */
    private String taskKey;

    /**
     * 类型
     */
    private String flowCommentType;

    /**
     * 审批人
     */
    private String assignee;

    /**
     * 审批人姓名
     */
    private String assigneeName;

    /**
     * 执行类型
     */
    private String executeType;

    /**
     * 执行类型[agree：同意（审批通过）、reject:驳回（默认驳回上一个任务）、rejectToTask:驳回到指定任务节点、revocation:撤回]
     */
    private String executeTypeValue;

    /**
     * 审批意见
     */
    private String commentContent;

    /**
     * 额外携带的内容
     */
    private String ext;

    /**
     * 变量参数
     */
    private Map<String, Object> variables;

}
