package com.easyflowable.core.domain.params;

import com.easyflowable.core.domain.enums.FlowExecuteType;
import com.easyflowable.core.domain.enums.FlowCommentType;
import lombok.Data;

import java.util.Map;

/**
 * @package: {@link com.easyflowable.core.domain.params}
 * @Date: 2024-10-09-10:46
 * @Description: 流程任务执行参数
 * @Author: MoJie
 */
@Data
public class FlowExecuteParam {

    /**
     * 任务id：必填
     */
    private String taskId;

    /**
     * 执行类型：必填
     */
    private FlowExecuteType executeType;

    /**
     * 是否驳回倒流程发起任务，默认:false
     */
    private boolean isInit = false;

    /**
     * 驳回到指定任务节点ID
     */
    private String rejectToTaskId;

    /**
     * 审批意见
     */
    private String commentContent;

    /**
     * 变量参数
     */
    private Map<String, Object> variables;

    /**
     * 审批人
     */
    private String assignee;

    /**
     * 审批人姓名
     */
    private String assigneeName;

    /**
     * 是否自动添加审批意见
     */
    private boolean isComment = true;

    /**
     * 审批类型
     */
    private FlowCommentType flowCommentType;

}
