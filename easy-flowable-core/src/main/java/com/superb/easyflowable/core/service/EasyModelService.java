package com.superb.easyflowable.core.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.superb.easyflowable.core.domain.entity.EasyModel;
import com.superb.easyflowable.core.domain.entity.EasyModelHistory;

import java.util.List;

/**
 * @package: {@link com.superb.easyflowable.core.service}
 * @Date: 2024-09-27-14:12
 * @Description:
 * @Author: MoJie
 */
public interface EasyModelService extends IService<EasyModel> {

    /**
     * 获取流程部署历史列表
     * @param modelId 流程模型ID
     * @return {@link List<EasyModelHistory>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:56:26
     */
    List<EasyModelHistory> listModelHistory(String modelId);

    /**
     * 获取流程部署历史列表
     * @param page 分页
     * @param modelHistory 查询条件
     * @return {@link Page<EasyModelHistory>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:57:52
     */
    Page<EasyModelHistory> modelHistory(Page<EasyModelHistory> page, EasyModelHistory modelHistory);

    /**
     * 保存流程部署历史
     * @param modelHistory 部署信息
     * @Author: MoJie
     * @Date: 2024-10-09 12:47:50
     */
    void saveHistory(EasyModelHistory modelHistory);

    /**
     * 根据模型ID删除部署历史
     * @param modelId 模型ID
     * @Author: MoJie
     * @Date: 2024-10-09 12:47:50
     */
    boolean deleteHistoryByModelId(String modelId);

    /**
     * @param historyId 发布历史ID
     * @return {@link boolean}
     * @Author: MoJie
     * @Date: 2024-10-12 11:15
     * @Description: 历史版本回退
     */
    boolean modelHistoryRollback(String historyId);
}
