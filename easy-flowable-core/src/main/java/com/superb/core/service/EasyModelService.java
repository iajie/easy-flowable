package com.superb.core.service;

import com.superb.core.domain.dto.Page;
import com.superb.core.domain.entity.EasyModel;

/**
 * {@link com.superb.core.service}
 *
 * @author MoJie
 * @since 1.0  2024-09-27-14:12
 */
public interface EasyModelService {

    /**
     * @param model 模型数据
     * @return {@link boolean}  新增模型
     * @author MoJie
     * @since 1.0  2024-10-22 12:39
     */
    boolean insert(EasyModel model);

    /**
     * @param model 模型
     * @return {@link boolean}  根据ID修改模型
     * @author MoJie
     * @since 1.0  2024-10-22 12:39
     */
    boolean updateById(EasyModel model);

    /**
     * @param id 主键
     * @return {@link EasyModel} 根据ID查询模型详情
     * @author MoJie
     * @since 1.0  2024-10-22 12:40
     */
    EasyModel getById(String id);

    /**
     * @param id 主键
     * @return {@link boolean} 根据ID删除模型
     * @author MoJie
     * @since 1.0  2024-10-22 12:41
     */
    boolean removeById(String id);

    /**
     * @param current 页码
     * @param size    页大小
     * @param model   模型查询参数
     * @return {@link Page} {@link EasyModel} 分页查询
     * @author MoJie
     * @since 1.0  2024-10-22 12:45
     */
    Page<EasyModel> queryPage(int current, int size, EasyModel model);
}
