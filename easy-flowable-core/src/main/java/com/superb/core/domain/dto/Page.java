package com.superb.core.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author MoJie
 * @since 1.0  2024-11-09 11:52
 */
@Data
public class Page<T> {

    /**
     * 总条数
     */
    private long total;

    /**
     * 记录数
     */
    private List<T> records;

}
