package com.superb.core.domain.params;

import lombok.Data;

/**
 * 流程作废参数
 * {@link com.superb.core.domain.params}
 * @since 1.0  2024-10-09-10:52
 * @author MoJie
 */
@Data
public class FlowCancellationParam {

    /**
     * 审批人：必填
     */
    private String assignee;

    /**
     * 审批人姓名：必填
     */
    private String assigneeName;

    /**
     * 任务ID：必填
     */
    private String taskId;

    /**
     * 流程实例ID：必填
     */
    private String processInstanceId;

    /**
     * 作废原因：必填
     */
    private String cancellationCause;
    
}
