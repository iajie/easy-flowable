package com.easyflowable.ui.resource;

import com.easyflowable.core.config.EasyFlowableUiConfig;
import com.easyflowable.core.constans.Constants;
import com.easyflowable.core.domain.dto.*;
import com.easyflowable.core.domain.params.FlowStartParam;
import com.easyflowable.core.service.EasyProcessInstanceService;
import com.easyflowable.ui.context.EasyFlowableContext;
import com.easyflowable.ui.model.PageParams;
import com.easyflowable.ui.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @package: {@link com.easyflowable.ui.resource}
 * @Date: 2024-10-09-16:24
 * @Description: 流程实例接口
 * @Author: MoJie
 */
@RestController
public class EasyProcessInstanceResource {

    @Autowired(required = false)
    private EasyProcessInstanceService processInstanceService;

    /**
     * 获取启动的流程实例 包含流程终止和激活的
     * @param processDefinitionId 流程定义ID
     * @return {@link List<FlowProcessInstance>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:26:52
     */
    @GetMapping(value = Constants.EASY_FLOWABLE + "/processInstance/list/{processDefinitionId}")
    public Result<List<FlowProcessInstance>> list(@PathVariable String processDefinitionId) {
        return Result.success(processInstanceService.getFlowInstanceListById(processDefinitionId));
    }

    /**
     * 流程实例状态设置 当实例状态为激活时会设置为终止，当实例状态为终止时状态会激活
     * @param processInstanceId 实例ID
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:27:37
     */
    @GetMapping(value = Constants.EASY_FLOWABLE + "/processInstance/stateSet/{processInstanceId}")
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
    @GetMapping(value = Constants.EASY_FLOWABLE + "/processInstance/backUserTasks/{processInstanceId}")
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
    @GetMapping(value = Constants.EASY_FLOWABLE + "/processInstance/executionHistory/{processInstanceId}")
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
    @PostMapping(Constants.EASY_FLOWABLE + "/processInstance/start")
    public Result<String> start(@RequestBody FlowStartParam param) {
        EasyFlowableUiConfig.User user = EasyFlowableContext.getUser();
        param.setStartUserId(user.getId());
        param.setStartUsername(user.getUsername());
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
    @GetMapping(Constants.EASY_FLOWABLE + "/processInstance/businessStatus")
    public Result<String> start(@RequestParam String processInstanceId, @RequestParam String status) {
        processInstanceService.updateProcessInstanceBusinessStatus(processInstanceId, status);
        return Result.success();
    }

    @GetMapping(Constants.EASY_FLOWABLE + "/processInstance/statics")
    public Result<Map<String, Object>> statics() {
        return Result.success(processInstanceService.statics());
    }

    /**
     * @param pageParams 分页查询参数
     * @return: {@link Result} {@link Page} {@link TodoTask}
     * @Author: MoJie
     * @Date: 2024/11/17 14:20
     * @Description: 待办任务分页查询
     */
    @PostMapping(Constants.EASY_FLOWABLE + "/processInstance/todoTaskPage")
    public Result<Page<DoneTask>> todoTaskPage(@RequestBody PageParams<String> pageParams) {
        String param = pageParams.getParams();
        Page<DoneTask> doneTaskPage = null;
        if ("todo".equals(param)) {
            doneTaskPage = processInstanceService.todoTasks(
                    null, pageParams.getCurrent(), pageParams.getSize());
        } else if ("done".equals(param)) {
            doneTaskPage = processInstanceService.doneTasks(
                    null, pageParams.getCurrent(), pageParams.getSize());
        } else if ("meTodo".equals(param)) {
            doneTaskPage = processInstanceService.todoTasksByUser(
                    null, pageParams.getCurrent(), pageParams.getSize());
        } else if ("meDone".equals(param)){
            doneTaskPage = processInstanceService.doneTasksByUser(
                    null, pageParams.getCurrent(), pageParams.getSize());
        }
        return Result.success(doneTaskPage);
    }
}
