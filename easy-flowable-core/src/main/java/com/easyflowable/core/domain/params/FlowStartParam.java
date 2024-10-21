package com.easyflowable.core.domain.params;

import lombok.Data;

import java.util.Map;

/**
 * @package: {@link com.easyflowable.core.domain.params}
 * @Date: 2024-10-09-10:25
 * @Description: 流程启动参数
 * @Author: MoJie
 */
@Data
public class FlowStartParam {

    /**
     * 流程标识：必填
     */
    private String flowKey;

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
     * @Description: 当你的流程模型设计开始节点为触发启动节点时，则需要跳过开始节点进入提交资料的节点
     */
    private boolean isSkipFirstNode = false;

    /**
     * 流程名称：必填
     */
    private String processName;
    
}
