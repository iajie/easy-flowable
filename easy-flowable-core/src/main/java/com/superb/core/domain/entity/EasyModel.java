package com.superb.core.domain.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 流程引擎模型
 * @since 1.0  2024-09-27-13:41
 * @author MoJie
 */
@Data
@Accessors(chain = true)
public class EasyModel {

    /**
     * 业务主键
     */
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
    private String modelEditorXml;

    /**
     * 流程图片
     */
    private String picture;

    /**
     * 0: bpmn图形化模型
     * 2：表单类型的流程模型
     * 3：应用程序类型的流程模型
     * 4：决策表类型的流程模型
     */
    private String modelType;

    /** 发布版本 */
    private Integer publishVersion;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 备注
     */
    private String remarks;

}
