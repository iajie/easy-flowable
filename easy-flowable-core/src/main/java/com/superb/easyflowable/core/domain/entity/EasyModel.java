package com.superb.easyflowable.core.domain.entity;

import java.util.Date;

/**
 * @package: {@link com.superb.easyflowable.core.domain.entity}
 * @Date: 2024-09-27-10:32
 * @Description: 模型实体
 * @Author: MoJie
 */
public interface EasyModel {

    /**
     * 业务主键
     */
    String getId();

    /**
     * 模型名称
     */
    String getName();

    /**
     * 模型唯一标识
     */
    String getKey();

    /**
     * 模型数据
     */
    String getModelEditorXml();

    /**
     * 缩略图
     */
    byte[] getThumbnail();

    /**
     * 0: bpmn图形化模型
     * 2：表单类型的流程模型
     * 3：应用程序类型的流程模型
     * 4：决策表类型的流程模型
     */
    Integer getModelType();

    /**
     * 数据版本
     */
    Integer getVersion();

    /**
     * 发布版本
     */
    Integer publishVersion();

    /**
     * 创建时间
     */
    Date getCreateTime();

    /**
     * 创建人
     */
    String getCreateBy();

    /**
     * 更新时间
     */
    Date getUpdateTime();

    /**
     * 更新人
     */
    String getUpdateBy();

    /**
     * 租户id
     */
    String getTenantId();

    /**
     * 所属部门id：如果您有模型数据权限，可以实现接口达到数据权限控制
     */
    String getOrganId();

    /**
     * 备注
     */
    String getRemarks();

}
