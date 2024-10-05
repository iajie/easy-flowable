package com.superb.easyflowable.core.domain.entity;

import java.util.Date;

/**
 * @package: {@link com.superb.easyflowable.core.domain.entity}
 * @Date: 2024-09-27-10:32
 * @Description: 模型发布历史
 * @Author: MoJie
 */
public interface EasyModelHistory {

    /**
     * 业务主键
     */
    String getId();

    /** 模型ID */
    String getModelId();

    /** 模型数据BPMNXML内容：以XML形式保存的流程定义信息 */
    String getModelEditorXml();

    /** 发布版本 */
    Integer getVersion();

    /** 创建时间 */
    Date getCreateTime();

    /** 创建人 */
    String getCreateBy();

    /** 所属部门id：如果您有模型数据权限，可以实现接口达到数据权限控制 */
    String getOrganId();

    /** 备注 */
    String getRemarks();

}
