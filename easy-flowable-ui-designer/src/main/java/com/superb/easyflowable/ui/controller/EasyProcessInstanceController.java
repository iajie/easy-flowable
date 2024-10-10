package com.superb.easyflowable.ui.controller;

import com.superb.easyflowable.core.domain.dto.FlowExecutionHistory;
import com.superb.easyflowable.core.domain.dto.FlowProcessInstance;
import com.superb.easyflowable.core.domain.dto.Option;
import com.superb.easyflowable.core.domain.params.FlowStartParam;
import com.superb.easyflowable.core.service.EasyFlowProcessInstanceService;
import com.superb.easyflowable.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @package: {@link com.superb.easyflowable.ui.controller}
 * @Date: 2024-10-09-16:24
 * @Description: 流程实例接口
 * @Author: MoJie
 */
@RestController
@RequestMapping("easy-flowable/processInstance")
public class EasyProcessInstanceController {

    @Autowired
    private EasyFlowProcessInstanceService processInstanceService;

    /**
     * 获取启动的流程实例 包含流程终止和激活的
     * @param flowKey 流程模型标识
     * @return {@link List<FlowProcessInstance>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:26:52
     */
    @GetMapping(value = "list/{flowKey}")
    public Result<List<FlowProcessInstance>> list(@PathVariable String flowKey) {
        return Result.success(processInstanceService.getFlowInstanceList(flowKey));
    }

    /**
     * 流程实例状态设置 当实例状态为激活时会设置为终止，当实例状态为终止时状态会激活
     * @param processInstanceId 实例ID
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:27:37
     */
    @GetMapping(value = "stateSet/{processInstanceId}")
    public Result<Boolean> stateSet(@PathVariable String processInstanceId) {
        return Result.success(processInstanceService.updateProcessInstanceState(processInstanceId));
    }

    /**
     * 根据流程实例Id获取可回退的节点
     * @param processInstanceId 实例ID
     * @return {@link List<Option>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:28:20
     */
    @GetMapping(value = "backUserTasks/{processInstanceId}")
    public Result<List<Option>> backUserTasks(@PathVariable String processInstanceId) {
        return Result.success(processInstanceService.getFlowBackUserTasks(processInstanceId));
    }

    /**
     * 根据流程实例Id获取流程执行历史
     * @param processInstanceId 实例ID
     * @return {@link List<FlowExecutionHistory>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:28:55
     */
    @GetMapping(value = "backUserTasks/{processInstanceId}")
    public Result<List<FlowExecutionHistory>> executionHistory(@PathVariable String processInstanceId) {
        return Result.success(processInstanceService.getFlowExecutionHistoryList(processInstanceId));
    }

    /**
     * 启动流程实例
     * @param param 启动参数
     * @return {@link Result<String>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:32:43
     */
    @PostMapping("start")
    public Result<String> start(@RequestBody FlowStartParam param) {
        return Result.success(processInstanceService.startProcessInstanceByKey(param));
    }

    /**
     * 编辑流程业务状态
     * @param processInstanceId 流程实例ID
     * @param status 业务表状态
     * @return {@link Result<String>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:32:43
     */
    @GetMapping("businessStatus")
    public Result<String> start(@RequestParam String processInstanceId, @RequestParam String status) {
        processInstanceService.updateProcessInstanceBusinessStatus(processInstanceId, status);
        return Result.success();
    }

    /**
     * 根据业务主键获取到对应的实例
     * @param id 业务主键
     * @return {@link Result<FlowProcessInstance>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:32:43
     */
    @GetMapping("businessKey/{id}")
    public Result<FlowProcessInstance> getProcessInstance(@PathVariable String id) {
        return Result.success(processInstanceService.getProcessInstance(id));
    }

}
