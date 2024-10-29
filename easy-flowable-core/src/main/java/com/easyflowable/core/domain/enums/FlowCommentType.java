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

    /** 正常进入下一节点 */
    START("0", "启动流程"),
    AGREE("1", "同意"),

    /** 回到发起节点 */
    REBUT("2", "拒绝"),
    REVOCATION("3", "撤回"),

    /** 回到 上一节点/指定节点 */
    REJECT("4", "驳回"),
    REJECT_TO_TASK("5", "驳回到指定节点"),

    /** 重新指定节点执行人 */
    DELEGATE("6", "委派"),
    /** 将任务转交给候选组完成(其他部门/岗位/角色处理) */
    ASSIGN("7", "转办"),

    /** 流程实例停止运行，可以激活 */
    STOP("8", "终止"),

    /** 在执行人之前增加一个人审批 */
    BEFORE_SIGN("9", "前加签"),
    /** 在执行人之后增加一个人审批 */
    AFTER_SIGN("10", "后加签"),

    /** 流程作废 */
    CANCELLATION("11", "作废"),

    ADD_COMMENT("12", "添加评论"),
    DEL_COMMENT("13", "删除评论"),
    RESUBMIT("14", "重新提交"),

    ;
    private final String code;
    private final String description;


}
