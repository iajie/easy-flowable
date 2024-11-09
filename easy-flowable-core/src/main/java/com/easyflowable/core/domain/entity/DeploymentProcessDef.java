package com.easyflowable.core.domain.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @package: {@link com.easyflowable.core.domain.dto}
 * @Date: 2024-10-09-9:25
 * @Description: 流程部署实例
 * @Author: MoJie
 */
@Data
@Accessors(chain = true)
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
    private String modelType;

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
    private boolean suspensionState;

    /**
     * 是否存在开始节点formKey: 0：否 ,1:是
     */
    private boolean hasStartFormKey;

    /**
     * 部署时间
     */
    private Date deploymentTime;
    
}
