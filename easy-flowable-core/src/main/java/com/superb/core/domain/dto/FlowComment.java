package com.superb.core.domain.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 任务执行意见
 * @since 1.0  2024-10-09-10:35
 * @author MoJie
 */
@Data
public class FlowComment {

    /**
     * 评论ID
     */
    private String commentId;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 任务ID（缺省）
     */
    private String taskId;

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
     * 委派/转办人
     */
    private String userId;

    /**
     * 审批意见
     */
    private String commentContent;

    /** 附件ID */
    private String attachmentId;

    /**
     * 额外携带的内容: 表单信息
     */
    private String ext;

    /**
     * 是否为表单
     */
    private boolean form;

    /**
     * 变量参数
     */
    private Map<String, Object> variables;

    /**
     * 评论时间
     */
    private Date commentTime;
}
