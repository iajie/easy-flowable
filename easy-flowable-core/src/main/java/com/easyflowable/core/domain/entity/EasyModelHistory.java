package com.easyflowable.core.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * @package: {@link com.easyflowable.core.domain.entity}
 * @Date: 2024-09-27-13:41
 * @Description: 流程引擎模型
 * @Author: MoJie
 */
@Data
@Table("easy_model_history")
public class EasyModelHistory {

    /**
     * 业务主键
     */
    @Id(keyType = KeyType.Generator)
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
     * 备注
     */
    private String remarks;
}
