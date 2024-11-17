package com.easyflowable.core.service;

import com.easyflowable.core.domain.dto.*;
import com.easyflowable.core.domain.params.FlowStartParam;

import java.util.List;
import java.util.Map;

/**
 * @package: {@link com.easyflowable.core.service}
 * @Date: 2024-10-09-10:23
 * @Description: 流程实例接口
 * @Author: MoJie
 */
public interface EasyProcessInstanceService {

    /**
     * 获取实例列表
     * @param flowKey 流程key
     * @return {@link List<FlowProcessInstance>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:28:36
     */
    default List<FlowProcessInstance> getFlowInstanceListByKey(String flowKey) {
        return getFlowInstanceList(flowKey, true, false);
    }

    /**
     * 获取实例列表
     * @param processDefinitionId 流程定义ID
     * @return {@link List<FlowProcessInstance>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:28:36
     */
    default List<FlowProcessInstance> getFlowInstanceListById(String processDefinitionId) {
        return getFlowInstanceList(processDefinitionId, true, true);
    }

    /**
     * 根据流程标识获取所有运行中的流程实例
     * @param key 标识
     * @param isFlow 是否为流程，否则为业务主键
     * @return {@link List<FlowProcessInstance>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:29:12
     */
    List<FlowProcessInstance> getFlowInstanceList(String key, boolean isFlow, boolean isProcessInstance);

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
    default FlowProcessInstance getProcessInstanceByBusinessKey(String businessKey) {
        return getFlowInstanceList(businessKey, false, false).get(0);
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

    /**
     * @param key 流程标识或流程部署ID
     * @param isDeployment 是否为部署
     * @Return: {@link List<FlowProcessInstanceHistory>}
     * @Author: MoJie
     * @Date: 2024/11/3 21:15
     * @Description: 根据流程标识或流程部署ID查询流程执行历史
     */
    List<FlowProcessInstanceHistory> getFlowInstanceHistoryList(String key, boolean isDeployment);

    /**
     * @param key 流程KEY
     * @Return: {@link List<FlowProcessInstanceHistory>}
     * @Author: MoJie
     * @Date: 2024/11/3 21:18
     * @Description: 根据流程KEY查询流程执行历史
     */
    default List<FlowProcessInstanceHistory> getFlowInstanceHistoryListByKey(String key) {
        return getFlowInstanceHistoryList(key, false);
    }

    /**
     * @param deploymentId 部署ID
     * @Return: {@link List<FlowProcessInstanceHistory>}
     * @Author: MoJie
     * @Date: 2024/11/3 21:19
     * @Description: 根据流程部署ID查询流程历史
     */
    default List<FlowProcessInstanceHistory> getFlowInstanceHistoryListByDeploymentId(String deploymentId) {
        return getFlowInstanceHistoryList(deploymentId, true);
    }

    /**
     * @param keywords 关键字
     * @param current 页码
     * @param size 页大小
     * @return: {@link List} {@link TodoTask}
     * @Author: MoJie
     * @Date: 2024/11/17 14:06
     * @Description: 任务待办列表
     */
    default Page<DoneTask> todoTasks(String keywords, int current, int size) {
        return this.todoTasks(keywords, current, size, false, false);
    }

    /**
     * @param keywords 关键字
     * @param current 页码
     * @param size 页大小
     * @return: {@link List} {@link TodoTask}
     * @Author: MoJie
     * @Date: 2024/11/17 14:06
     * @Description: 任务待办列表
     */
    default Page<DoneTask> doneTasks(String keywords, int current, int size) {
        return this.todoTasks(keywords, current, size, true, false);
    }

    default Page<DoneTask> todoTasksByUser(String userId, int current, int size) {
        return this.todoTasks(userId, current, size, false, true);
    }

    default Page<DoneTask> doneTasksByUser(String userId, int current, int size) {
        return this.todoTasks(userId, current, size, true, true);
    }

    /**
     * @param keywords 关键字
     * @param current 页码
     * @param size 页大小
     * @param finished 是否已结束
     * @param isMe 办理人
     * @return: {@link List} {@link TodoTask}
     * @Author: MoJie
     * @Date: 2024/11/17 14:06
     * @Description: 任务待办列表
     */
    Page<DoneTask> todoTasks(String keywords, int current, int size, Boolean finished, boolean isMe);

    /**
     * @return: {@link Map} {@link Object}
     * @Author: MoJie
     * @Date: 2024/11/17 15:06
     * @Description: 统计
     */
    Map<String, Object> statics();
}
