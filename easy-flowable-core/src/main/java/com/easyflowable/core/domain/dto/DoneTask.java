package com.easyflowable.core.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @Author: MoJie
 * @Date: 2024-11-17 13:56
 * @Description: 已办
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
