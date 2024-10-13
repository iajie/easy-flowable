package online.easyflowable.core.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @package: {@link online.easyflowable.core.domain.entity}
 * @Date: 2024-10-09-9:51
 * @Description: 流程定义
 * @Author: MoJie
 */
@Data
@Table("act_re_procdef")
public class ActReProcessDef {

    /**
     * 流程定义ID
     */
    @Column(value = "ID_")
    private String procdefId;

    /**
     * 版本号
     */
    @Column(value = "REV_")
    private Integer rev = 0;

    /**
     * 流程模型名称
     */
    @Column(value = "NAME_")
    private String name;

    /**
     * 流程模型标识
     */
    @Column(value = "KEY_")
    private String key;

    /**
     * 分类 流程定义的Namespace就是类别
     */
    @Column(value = "CATEGORY_")
    private String modelType;

    /**
     * 版本
     */
    @Column(value = "VERSION_")
    private String version;

    /**
     * 部署ID
     */
    @Column(value = "DEPLOYMENT_ID_")
    private String deploymentId;

    /**
     * 资源名称 流程bpmn文件名称
     */
    @Column(value = "RESOURCE_NAME_")
    private String resourceName;

    /**
     * 图片资源名称
     */
    @Column(value = "DGRM_RESOURCE_NAME_")
    private String dgrmResourceName;

    /**
     * 描述
     */
    @Column(value = "DESCRIPTION_")
    private String description;

    /**
     * 是否存在开始节点formKey: 0：否 ,1:是
     */
    @Column(value = "HAS_START_FORM_KEY_")
    private Integer hasStartFormKey;

    /**
     * 拥有图形信息
     */
    @Column(value = "HAS_GRAPHICAL_NOTATION_")
    private Integer hasGraphicalNotation;

    /**
     * 流程定义状态: 1:激活 , 2:中止
     */
    @Column(value = "SUSPENSION_STATE_")
    private Integer suspensionState;

    /**
     * 租户ID
     */
    @Column(value = "TENANT_ID_")
    private String tenantId;

    /**
     * 流程引擎的版本
     */
    @Column(value = "ENGINE_VERSION_")
    private String engineVersion;

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
     * 衍生版本
     */
    @Column(value = "DERIVED_VERSION_")
    private String derivedVersion;

}
