package com.superb.esayflowable.ui.model;

import com.mybatisflex.core.paginate.Page;
import lombok.Data;

/**
 * @package: {@link com.superb.esayflowable.ui.model}
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

    /** 分页对象 */
    public Page<T> getPage() {
        if (current == null) {
            current = 1;
        }
        if (size == null) {
            size = 10;
        }
        return new Page<>(current, size);
    }

}
