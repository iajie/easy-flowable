package com.superb.ui.model;

import lombok.Data;

/**
 * @package: {@link com.superb.ui.model}
 * @Date: 2024-09-27-14:20
 * @Description: 分页查询
 * @Author: MoJie
 */
@Data
public class PageParams<T> {

    /** 页码 */
    private Integer current;

    /** 页大小 */
    private Integer pageSize;

    /** 查询参数 */
    private T params;

}
