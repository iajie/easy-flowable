package com.easyflowable.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @package: {@link com.easyflowable.core.domain.enums}
 * @Date: 2024-10-09-10:47
 * @Description: 审批意见
 * @Author: MoJie
 */
@Getter
@AllArgsConstructor
public enum FlowCommentType {

    NORMAL("1", "正常意见"),
    APPROVE("2", "审批意见"),
    REBUT("3", "退回意见"),
    REJECT("4", "驳回意见"),
    REVOCATION("5", "撤回意见"),
    DELEGATE("6", "委派意见"),
    ASSIGN("7", "转办意见"),
    STOP("8", "终止流程"),
    CANCELLATION("9", "作废原因");

    private final String code;
    private final String description;


}
