package com.superb.core.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * 流程历史
 * @author MoJie
 * @since 1.0  2024-11-03 20:59
 */
@Data
public class FlowProcessInstanceHistory {

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 流程开始时间
     */
    private Date startTime;

    /**
     * 流程开始时间
     */
    private Date endTime;

    /**
     * 流程耗时
     */
    private Long duration;

    /**
     * 发起人ID
     */
    private String startUserId;

    /**
     * 业务主键
     */
    private String businessKey;

    /**
     * 实例名称
     */
    private String name;

    /**
     * 业务状态
     */
    private String businessKeyStatus;

    /**
     * 部署ID
     */
    private String deploymentId;

    /**
     * 流程实例运行版本
     */
    private Integer processInstanceVersion;

    /**
     * 作废原因
     */
    private String cancellationCause;
}
