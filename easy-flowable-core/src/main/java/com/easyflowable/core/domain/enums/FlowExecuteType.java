package com.easyflowable.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @package: {@link com.easyflowable.core.domain.enums}
 * @Date: 2024-10-09-10:38
 * @Description: 流程执行类型
 * @Author: MoJie
 */
@Getter
@AllArgsConstructor
public enum FlowExecuteType {

    START("start", "启动流程"),
    RESUBMIT("resubmit", "重新提交"),
    REVOCATION("revocation", "撤回申请"),
    GATEWAY("gateway", "流程网关流转"),
    AGREE("agree", "审核通过"),
    REJECT("reject", "驳回"),
    REJECT_TO_TASK("rejectToTask", "驳回到指定任务节点"),
    CANCELLATION("cancellation", "流程作废");
    private final String code;
    private final String description;

    /**
     * 根据code获取去INfo
     * @param code code
     * @return {@link FlowExecuteType}
     * @Author: MoJie
     * @Date: 2024-10-09 10:38:44
     */
    public static FlowExecuteType of(String code) {
        for (FlowExecuteType executeType : FlowExecuteType.values()) {
            if (code.equals(executeType.getCode())) {
                return executeType;
            }
        }
        return null;
    }

    /**
     * 根据code获取去INfo
     * @param code code
     * @return {@link String}
     * @Author: MoJie
     * @Date: 2024-10-09 10:38:44
     */
    public static String as(String code) {
        FlowExecuteType executeType = of(code);
        if (executeType == null) {
            return null;
        }
        return executeType.description;
    }

}
