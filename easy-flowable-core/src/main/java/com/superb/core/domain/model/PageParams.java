package com.superb.core.domain.model;

import lombok.Data;

/**
 * 分页查询
 * @since 1.0  2024-09-27-14:20
 * @author MoJie
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
