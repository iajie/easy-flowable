package com.superb.easyflowable.ui.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.easyflowable.core.domain.dto.DeploymentProcessDef;
import com.superb.easyflowable.core.domain.dto.FlowUserTask;
import com.superb.easyflowable.core.domain.entity.ActReDeployment;
import com.superb.easyflowable.core.domain.entity.ActReProcessDef;
import com.superb.easyflowable.core.service.EasyFlowDeploymentService;
import com.superb.easyflowable.core.utils.StringUtils;
import com.superb.easyflowable.ui.model.PageParams;
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
 * @Date: 2024-10-09-9:18
 * @Description: 流程部署管理
 * @Author: MoJie
 */
@RestController
@RequestMapping("easy-flowable/deployment")
public class EasyDeploymentController {

    @Autowired
    private EasyFlowDeploymentService deploymentService;

    /**
     * 流程部署定义分页查询
     * @param pageParams 分页参数
     * @return {@link Page<DeploymentProcessDef>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:15:19
     */
    @PostMapping("page")
    public Result<Page<DeploymentProcessDef>> page(@RequestBody PageParams<DeploymentProcessDef> pageParams) {
        DeploymentProcessDef params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("rd.ID_ AS id", "rp.ID_ AS processDefinitionId", "rd.NAME_ AS name",
                "rp.HAS_START_FORM_KEY_ AS hasStartFormKey", "rd.DEPLOY_TIME_ AS deploymentTime", "rd.KEY_ AS `key`",
                "rd.CATEGORY_ AS modelType", "rd.TENANT_ID_ AS tenantId", "rp.VERSION_ AS version",
                "rp.SUSPENSION_STATE_ AS suspensionState");
        // 主表
        queryWrapper.from(ActReDeployment.class).as("rd");
        queryWrapper.like(ActReDeployment::getName, params.getName(), StringUtils.isNotBlank(params.getName()));
        queryWrapper.like(ActReDeployment::getFlowKey, params.getKey(), StringUtils.isNotBlank(params.getKey()));
        queryWrapper.eq(ActReDeployment::getModelType, params.getModelType(), params.getModelType() != null);
        queryWrapper.leftJoin(ActReProcessDef.class).as("rp").on(ActReDeployment::getId, ActReProcessDef::getDeploymentId);
        queryWrapper.orderBy(ActReDeployment::getDeploymentTime);
        return Result.success(deploymentService.pageAs(pageParams.getPage(), queryWrapper, DeploymentProcessDef.class));
    }

    /**
     * 通过ModelId部署流程
     * @param modelId 流程模型ID
     * @return {@link Result<String>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:19:05
     */
    @GetMapping(value = "/{modelId}")
    public Result<String> deploymentModel(@PathVariable String modelId) {
        return Result.success("流程部署成功！", deploymentService.deploymentModel(modelId));
    }

    /**
     * 删除流程定义
     * @param deploymentId 实例id
     * @param cascade 是否级联删除
     * @return {@link Result}
     * @Author: MoJie
     * @Date: 2024-10-09 16:20:04
     */
    @GetMapping(value = "deleteDeployment")
    public Result<String> deleteDeployment(@RequestParam String deploymentId, @RequestParam Boolean cascade) {
        if (cascade == null) {
            cascade = false;
        }
        deploymentService.deleteDeployment(deploymentId, cascade);
        return Result.success("流程定义删除成功！");
    }

    /**
     * 流程定义状态设置
     * 设置流程状态（当流程为：1激活（默认设置为终止流程）2：中止（挂起）（默认设置为激活流程））
     * @param processDefinitionId 流程定义id
     * @return {@link Result<String>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:21:06
     */
    @GetMapping(value = "deploymentState/{processDefinitionId}")
    public Result<String> deploymentState(@PathVariable String processDefinitionId) {
        return Result.success("流程定义状态设置成功！", deploymentService.deploymentState(processDefinitionId));
    }

    /**
     * 流程定义用户任务节点 当前流程定义中存在用户任务的节点列表
     * @param flowKey 流程定义key
     * @return {@link List<FlowUserTask>>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:21:50
     */
    @GetMapping(value = "flowUserList/{flowKey}")
    public Result<List<FlowUserTask>> getFlowUserTaskList(@PathVariable String flowKey) {
        return Result.success(deploymentService.getAllFlowUserTask(flowKey));
    }
}
