package com.superb.solon.resource;

import com.superb.core.constans.Constants;
import com.superb.core.constans.EasyFlowableContext;
import com.superb.core.domain.dto.*;
import com.superb.core.domain.entity.EasyFlowableUser;
import com.superb.core.domain.model.PageParams;
import com.superb.core.domain.model.Result;
import com.superb.core.domain.params.FlowStartParam;
import com.superb.core.service.EasyProcessInstanceService;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;

import java.util.List;
import java.util.Map;

/**
 * @package: {@link com.superb.ui.resource}
 * @Date: 2024-10-09-16:24
 * @Description: 流程实例接口
 * @Author: MoJie
 */
@Controller
@Mapping(Constants.EASY_FLOWABLE + "/processInstance")
public class EasyProcessInstanceResource {

    @Inject
    private EasyProcessInstanceService processInstanceService;

    /**
     * 获取启动的流程实例 包含流程终止和激活的
     * @param processDefinitionId 流程定义ID
     * @return {@link List<FlowProcessInstance>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:26:52
     */
    @Mapping(value = "/list/{processDefinitionId}", method = MethodType.GET)
    public Result<List<FlowProcessInstance>> list(@Path("processDefinitionId") String processDefinitionId) {
        return Result.success(processInstanceService.getFlowInstanceListById(processDefinitionId));
    }

    /**
     * 流程实例状态设置 当实例状态为激活时会设置为终止，当实例状态为终止时状态会激活
     * @param processInstanceId 实例ID
     * @return {@link Result<Boolean>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:27:37
     */
    @Mapping(value = "/stateSet/{processInstanceId}", method = MethodType.GET)
    public Result<Boolean> stateSet(@Param String processInstanceId) {
        return Result.success(processInstanceService.updateProcessInstanceState(processInstanceId));
    }

    /**
     * 根据流程实例Id获取可回退的节点
     * @param processInstanceId 实例ID
     * @return {@link List< Option >}
     * @Author: MoJie
     * @Date: 2024-10-09 16:28:20
     */
    @Mapping(value = "/backUserTasks/{processInstanceId}", method = MethodType.GET)
    public Result<List<Option>> backUserTasks(@Param String processInstanceId) {
        return Result.success(processInstanceService.getFlowBackUserTasks(processInstanceId));
    }

    /**
     * 根据流程实例Id获取流程执行历史
     * @param processInstanceId 实例ID
     * @return {@link List< FlowExecutionHistory >}
     * @Author: MoJie
     * @Date: 2024-10-09 16:28:55
     */
    @Mapping(value = "/executionHistory/{processInstanceId}", method = MethodType.GET)
    public Result<List<FlowExecutionHistory>> executionHistory(@Param String processInstanceId) {
        return Result.success(processInstanceService.getFlowExecutionHistoryList(processInstanceId));
    }

    /**
     * 启动流程实例
     * @param param 启动参数
     * @return {@link Result<String>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:32:43
     */
    @Mapping(value = "/start", method = MethodType.POST)
    public Result<String> start(@Body FlowStartParam param) {
        EasyFlowableUser user = EasyFlowableContext.getUser();
        param.setStartUserId(user.getUserId());
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
    @Mapping(value = "/businessStatus", method = MethodType.GET)
    public Result<String> start(@Param String processInstanceId, @Param String status) {
        processInstanceService.updateProcessInstanceBusinessStatus(processInstanceId, status);
        return Result.success();
    }

    /**
     * @param processInstanceId 流程实例ID
     * @return: {@link Result} {@link Map} {@link Object}
     * @Author: MoJie
     * @Date: 2024/11/25 18:43
     * @Description: 流程动态
     */
    @Get
    @Mapping("processDynamics")
    public Result<Map<String, Object>> processDynamics(@Param String processInstanceId, @Param String processDefinitionId) {
        return Result.success(processInstanceService.processDynamics(processInstanceId, processDefinitionId));
    }

    /**
     * @param nodeId 流程实例ID
     * @return: {@link Result} {@link Map} {@link Object}
     * @Author: MoJie
     * @Date: 2024/11/25 18:43
     * @Description: 流程动态
     */
    @Get
    @Mapping("nodeInfo/{nodeId}")
    public Result<Map<String, Object>> nodeInfo(@Path String nodeId) {
        return Result.success(processInstanceService.nodeInfo(nodeId));
    }

    @Mapping(value = "/statics", method = MethodType.GET)
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
    @Mapping(value = "/todoTaskPage", method = MethodType.POST)
    public Result<Page<DoneTask>> todoTaskPage(@Body PageParams<String> pageParams) {
        String param = pageParams.getParams();
        Page<DoneTask> doneTaskPage = null;
        if ("todo".equals(param)) {
            doneTaskPage = processInstanceService.todoTasks(
                    null, pageParams.getCurrent(), pageParams.getPageSize());
        } else if ("done".equals(param)) {
            doneTaskPage = processInstanceService.doneTasks(
                    null, pageParams.getCurrent(), pageParams.getPageSize());
        } else if ("meTodo".equals(param)) {
            doneTaskPage = processInstanceService.todoTasksByUser(
                    null, pageParams.getCurrent(), pageParams.getPageSize());
        } else if ("meDone".equals(param)){
            doneTaskPage = processInstanceService.doneTasksByUser(
                    null, pageParams.getCurrent(), pageParams.getPageSize());
        }
        return Result.success(doneTaskPage);
    }
}
