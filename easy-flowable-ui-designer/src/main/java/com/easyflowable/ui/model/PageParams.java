package com.easyflowable.ui.model;

import com.easyflowable.core.domain.dto.Page;
import lombok.Data;

/**
 * @package: {@link com.easyflowable.ui.model}
 * @Date: 2024-09-27-14:20
 * @Description: 分页查询
 * @Author: MoJie
 */
@Data
public class PageParams<T> {

    /** 页码 */
    private Integer current;

    /** 页大小 */
    private Integer size;

    /** 查询参数 */
    private T params;

}
