package com.easyflowable.core.service;

import com.easyflowable.core.domain.dto.FlowExecutionHistory;
import com.easyflowable.core.domain.dto.FlowProcessInstance;
import com.easyflowable.core.domain.dto.Option;
import com.easyflowable.core.domain.params.FlowStartParam;

import java.util.List;

/**
 * @package: {@link com.easyflowable.core.service}
 * @Date: 2024-10-09-10:23
 * @Description: 流程实例接口
 * @Author: MoJie
 */
public interface EasyFlowProcessInstanceService {

    /**
     * 获取实例列表
     * @param flowKey 流程key
     * @return {@link List<FlowProcessInstance>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:28:36
     */
    default List<FlowProcessInstance> getFlowInstanceList(String flowKey) {
        return getFlowInstanceList(flowKey, true);
    }

    /**
     * 根据流程标识获取所有运行中的流程实例
     * @param key 标识
     * @param isFlow 是否为流程，否则为业务主键
     * @return {@link List<FlowProcessInstance>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:29:12
     */
    List<FlowProcessInstance> getFlowInstanceList(String key, boolean isFlow);

    /**
     * 启动流程实例
     * @param startParam {@link FlowStartParam} 流程启动方法
     * @return {@link String}
     * @Author: MoJie
     * @Date: 2024-10-09 10:31:19
     */
    String startProcessInstanceByKey(FlowStartParam startParam);

    /**
     * 更新流程实例中业务的状态
     * @param processInstanceId 流程实例ID
     * @param status 状态
     * @Author: MoJie
     * @Date: 2024-10-09 10:31:33
     */
    void updateProcessInstanceBusinessStatus(String processInstanceId, String status);

    /**
     * 根据业务主键获取流程实例
     * @param businessKey 业务主键
     * @return {@link FlowProcessInstance}
     * @Author: MoJie
     * @Date: 2024-10-09 10:31:51
     */
    default FlowProcessInstance getProcessInstance(String businessKey) {
        return getFlowInstanceList(businessKey, false).get(0);
    }

    /**
     * 执行流程可回退的用户任务节点
     * @param processInstanceId 流程实例ID
     * @return {@link List<Option>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:32:09
     */
    List<Option> getFlowBackUserTasks(String processInstanceId);

    /**
     * 获取流程执行历史记录
     * @param processInstanceId 流程实例ID
     * @return {@link List<FlowExecutionHistory>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:43:24
     */
    List<FlowExecutionHistory> getFlowExecutionHistoryList(String processInstanceId);

    /**
     * 修改实例状态
     * @param processInstanceId 实例ID
     * @return {@link boolean}
     * @Author: MoJie
     * @Date: 2024-10-09 10:43:39
     */
    boolean updateProcessInstanceState(String processInstanceId);

    /**
     * 根据流程实例ID获取上一个用户任务节点KEY
     * @param processInstanceId 流程实例ID
     * @return {@link String}
     * @Author: MoJie
     * @Date: 2024-10-09 10:54:31
     */
    String getUpNodeKey(String processInstanceId);
}
