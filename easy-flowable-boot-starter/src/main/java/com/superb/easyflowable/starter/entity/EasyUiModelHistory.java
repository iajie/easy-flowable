package com.superb.easyflowable.starter.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.superb.easyflowable.core.domain.entity.EasyModel;
import com.superb.easyflowable.core.domain.entity.EasyModelHistory;
import lombok.Setter;

import java.util.Date;

/**
 * @package: {@link com.superb.easyflowable.starter.entity}
 * @Date: 2024-09-27-13:41
 * @Description: 流程引擎模型
 * @Author: MoJie
 */
@Setter
@Table("easy_model_history")
public class EasyUiModelHistory implements EasyModelHistory {

    /**
     * 业务主键
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 模型数据
     */
    private String modelEditorXml;

    /**
     * 发布版本
     */
    private Integer version;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

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
    public String getModelEditorXml() {
        return modelEditorXml;
    }

    @Override
    public Integer getVersion() {
        return version;
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
    public String getOrganId() {
        return organId;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    @Override
    public String getModelId() {
        return modelId;
    }
}
