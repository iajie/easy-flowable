package com.easyflowable.core.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * @package: {@link com.easyflowable.core.domain.dto}
 * @Date: 2024-10-09-9:25
 * @Description: 流程部署实例
 * @Author: MoJie
 */
@Data
public class DeploymentProcessDef {

    /**
     * 部署id
     */
    private String id;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 部署名称
     */
    private String name;

    /**
     * 流程标识
     */
    private String key;

    /**
     * 分类
     */
    private Integer modelType;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 实例版本
     */
    private Integer version;

    /**
     * 流程定义状态: 1:激活 , 2:中止
     */
    private Integer suspensionState;

    /**
     * 是否存在开始节点formKey: 0：否 ,1:是
     */
    private Integer hasStartFormKey;

    /**
     * 部署时间
     */
    private Date deploymentTime;
    
}
