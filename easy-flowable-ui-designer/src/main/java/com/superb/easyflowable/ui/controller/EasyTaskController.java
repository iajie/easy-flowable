package com.superb.easyflowable.ui.controller;

import com.superb.easyflowable.core.domain.dto.FlowUserTask;
import com.superb.easyflowable.core.domain.params.FlowCancellationParam;
import com.superb.easyflowable.core.domain.params.FlowExecuteParam;
import com.superb.easyflowable.core.service.EasyFlowTaskService;
import com.superb.easyflowable.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @package: {@link com.superb.easyflowable.ui.controller}
 * @Date: 2024-10-09-17:03
 * @Description:
 * @Author: MoJie
 */
@RestController
@RequestMapping("easy-flowable/task")
public class EasyTaskController {

    @Autowired
    private EasyFlowTaskService taskService;

    /**
     * 执行任务
     * @param param {@link FlowExecuteParam} 执行参数
     * @return {@link List<FlowUserTask>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:05:06
     */
    @PostMapping(value = "execute")
    public Result<List<FlowUserTask>> taskUserList(@RequestBody FlowExecuteParam param) {
        taskService.executeNextStep(param);
        return Result.success();
    }

    /**
     * 流程作废
     * @param cancellation 作废流程
     * @return {@link List<FlowUserTask>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:06:15
     */
    @PostMapping(value = "cancellation")
    public Result<List<FlowUserTask>> taskUserList(@RequestBody FlowCancellationParam cancellation) {
        taskService.cancellationProcessInstance(cancellation);
        return Result.success();
    }

    /**
     * 获取当前任务节点执行人(候选人)
     * @param taskId 任务ID
     * @return {@link List<String>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:06:59
     */
    @GetMapping(value = "executors/{taskId}")
    public Result<List<String>> getUserTaskExecutors(@PathVariable String taskId) {
        return Result.success(taskService.getUserTaskExecutors(taskId, false));
    }

    /**
     * 获取当前任务节点执行部门(候选组)
     * @param taskId 任务ID
     * @return {@link List<String>>}
     * @Author: MoJie
     * @Date: 2024-10-09 17:08:34
     */
    @GetMapping(value = "executeOrgan/{taskId}")
    public Result<List<String>> executeOrgan(@PathVariable String taskId) {
        return Result.success(taskService.getUserTaskOrganIds(taskId));
    }

}
