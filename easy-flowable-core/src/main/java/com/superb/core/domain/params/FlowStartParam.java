package com.superb.core.domain.params;

import lombok.Data;

import java.util.Map;

/**
 * 流程启动参数
 * {@link com.superb.core.domain.params}
 * @since 1.0  2024-10-09-10:25
 * @author MoJie
 */
@Data
public class FlowStartParam {

    /**
     * 流程标识：非必填
     */
    private String flowKey;

    /**
     * 流程定义ID：必填
     */
    private String processDefinitionId;

    /**
     * 业务主键：必填
     */
    private String businessKey;

    /**
     * 流程发起人
     */
    private String startUserId;
    /**
     * 流程发起人名称
     */
    private String startUsername;

    /**
     * 流程变量：全局变量，会伴随流程结束(流程发起人...)
     */
    private Map<String, Object> variables;

    /**
     * 是否跳过第一(开始)节点
     * @description 当你的流程模型设计开始节点为触发启动节点时，则需要跳过开始节点进入提交资料的节点
     */
    private boolean skipFirstNode = false;

    /**
     * 流程名称：必填
     */
    private String processName;

    /**
     * 是否为表单流程
     */
    private boolean startFormData = false;

    /**
     * 表单信息：为JSON字符串/由@EasyItem-后续会使用到
     */
    private Object formData;
}
