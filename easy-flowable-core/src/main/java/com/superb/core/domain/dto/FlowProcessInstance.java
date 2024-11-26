package com.superb.core.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * 运行中的流程实例
 * @since 1.0  2024-10-09-10:23
 * @author MoJie
 */
@Data
public class FlowProcessInstance {

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 流程定义ID
     */
    private Date startTime;

    /**
     * 发起人ID
     */
    private String startUserId;

    /**
     * 流程实例状态：true终止，false激活
     */
    private boolean status = false;

    /**
     * 业务主键
     */
    private String businessKey;

    /**
     * 实例名称
     */
    private String name;

    /**
     * 租户信息
     */
    private String tenantId;

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
     * 当前运行的任务ID
     */
    private String taskId;

    /**
     * 当前运行的任务ID
     */
    private String taskIds;

}
