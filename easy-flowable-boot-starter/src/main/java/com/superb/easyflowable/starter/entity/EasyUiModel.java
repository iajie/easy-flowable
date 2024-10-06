package com.superb.easyflowable.starter.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.superb.easyflowable.core.domain.entity.EasyModel;
import lombok.Setter;

import java.util.Date;

/**
 * @package: {@link com.superb.easyflowable.starter.entity}
 * @Date: 2024-09-27-13:41
 * @Description: 流程引擎模型
 * @Author: MoJie
 */
@Setter
@Table("easy_model")
public class EasyUiModel implements EasyModel {

    /**
     * 业务主键
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型唯一标识
     */
    private String key;

    /**
     * 模型数据
     */
    @Column(isLarge = true)
    private String modelEditorXml;

    private byte[] thumbnail;

    /**
     * 0: bpmn图形化模型
     * 2：表单类型的流程模型
     * 3：应用程序类型的流程模型
     * 4：决策表类型的流程模型
     */
    private Integer modelType;

    /**
     * 乐观锁
     */
    @Column(version = true)
    private Integer version;
    /** 发布版本 */
    private Integer publishVersion;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 所属部门id：如果您有模型数据权限，可以实现接口达到数据权限控制
     */
    private String organId;

    /**
     * 备注
     */
    private String remarks;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getModelEditorXml() {
        return modelEditorXml;
    }

    @Override
    public byte[] getThumbnail() {
        return this.thumbnail;
    }

    @Override
    public Integer getModelType() {
        return modelType;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public Integer publishVersion() {
        return this.publishVersion;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public String getUpdateBy() {
        return updateBy;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public String getOrganId() {
        return organId;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }
}
