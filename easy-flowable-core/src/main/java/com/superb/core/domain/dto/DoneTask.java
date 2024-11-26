package com.superb.core.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 已办实体
 * @author MoJie
 * @since 1.0  2024-11-17 13:56
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DoneTask extends TodoTask {

    /**
     * 流程状态: 0进行中。1完成，2已作废
     */
    private Integer status;

    /**
     * 结束时间
     */
    private Date endTime;

}
