package com.superb.core.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: MoJie
 * @Date: 2024-11-09 11:52
 * @Description:
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
