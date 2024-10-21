package com.easyflowable.core.domain.entity;

import com.mybatisflex.annotation.Column;
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
@Table("easy_model")
public class EasyModel {

    /**
     * 业务主键
     */
    @Id(keyType = KeyType.Generator)
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

    private String thumbnail;

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
}
