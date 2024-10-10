package com.superb.easyflowable.core.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * @package: {@link com.superb.easyflowable.core.domain.entity}
 * @Date: 2024-10-09-10:03
 * @Description: 流程部署
 * @Author: MoJie
 */
@Data
@Table("act_re_deployment")
public class ActReDeployment {

    /**
     * 流程部署id
     */
    @Column(value = "ID_")
    private String id;

    /**
     * 流程名称
     */
    @Column(value = "NAME_")
    private String name;

    /**
     * 流程key
     */
    @Column(value = "KEY_")
    private String flowKey;

    /**
     * 流程分类
     */
    @Column(value = "CATEGORY_")
    private String modelType;

    /**
     * 流程引擎的版本
     */
    @Column(value = "ENGINE_VERSION_")
    private String version;

    /**
     * 部署时间
     */
    @Column(value = "DEPLOY_TIME_")
    private Date deploymentTime;

    /**
     * 租户ID
     */
    @Column(value = "TENANT_ID_")
    private String tenantId;

    /**
     * 来源
     */
    @Column(value = "DERIVED_FROM_")
    private String derivedFrom;

    /**
     * 来源
     */
    @Column(value = "DERIVED_FROM_ROOT_")
    private String derivedFromRoot;

    /**
     * 父级部署ID
     */
    @Column(value = "PARENT_DEPLOYMENT_ID_")
    private String parentDeploymentId;
    
}
