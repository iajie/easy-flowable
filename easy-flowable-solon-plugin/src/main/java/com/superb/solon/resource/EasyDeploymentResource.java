package com.superb.solon.resource;

import com.superb.core.constans.Constants;
import com.superb.core.domain.dto.FlowUserTask;
import com.superb.core.domain.dto.Page;
import com.superb.core.domain.entity.DeploymentProcessDef;
import com.superb.core.domain.model.PageParams;
import com.superb.core.domain.model.Result;
import com.superb.core.service.EasyDeploymentService;
import lombok.SneakyThrows;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.List;

/**
 * @package: {@link com.superb.ui.resource}
 * @Date: 2024-10-09-9:18
 * @Description: 流程部署管理
 * @Author: MoJie
 */
@Controller
@Mapping(Constants.EASY_FLOWABLE + "/deployment")
public class EasyDeploymentResource {

    @Inject
    private EasyDeploymentService deploymentService;

    /**
     * 流程部署定义分页查询
     * @param pageParams 分页参数
     * @return {@link Page<DeploymentProcessDef>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:15:19
     */
    @Mapping(value = "/page", method = MethodType.POST)
    public Result<Page<DeploymentProcessDef>> page(@Body PageParams<DeploymentProcessDef> pageParams) {
        return Result.success(deploymentService.page(pageParams.getCurrent(), pageParams.getPageSize(), pageParams.getParams()));
    }

    /**
     * 通过ModelId部署流程
     * @param modelId 流程模型ID
     * @return {@link Result<String>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:19:05
     */
    @Mapping(value = "/{modelId}", method = MethodType.GET)
    public Result<String> deploymentModel(@Param String modelId) {
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
    @Mapping(value = "/deleteDeployment", method = MethodType.GET)
    public Result<String> deleteDeployment(@Param String deploymentId, @Param Boolean cascade) {
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
    @Mapping(value = "State/{processDefinitionId}", method = MethodType.GET)
    public Result<String> deploymentState(@Param String processDefinitionId) {
        return Result.success("流程定义状态设置成功！", deploymentService.deploymentState(processDefinitionId));
    }

    /**
     * 流程定义用户任务节点 当前流程定义中存在用户任务的节点列表
     * @param flowKey 流程定义key
     * @return {@link List<FlowUserTask>>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:21:50
     */
    @Mapping(value = "/flowUserList/{flowKey}", method = MethodType.GET)
    public Result<List<FlowUserTask>> getFlowUserTaskList(@Param String flowKey) {
        return Result.success(deploymentService.getAllFlowUserTask(flowKey));
    }

    /**
     * 流程部署图片
     * @param processDefinitionId 流程定义id
     * @Author: MoJie
     * @Date: 2024-10-09 16:21:50
     */
    @SneakyThrows
    @Get
    @Mapping("Image/{processDefinitionId}")
    public void deploymentImage(@Param String processDefinitionId, Context ctx) {
        InputStream inputStream = deploymentService.getFlowImage(processDefinitionId);
        ctx.contentType("image/png");
        ImageIO.write(ImageIO.read(inputStream), "png", ctx.outputStream());
    }

    /**
     * 流程部署的XML
     * @param processDefinitionId 流程定义id
     * @Author: MoJie
     * @Date: 2024-10-09 16:21:50
     */
    @Get
    @Mapping("Xml/{processDefinitionId}")
    public Result<String> deploymentXml(@Param String processDefinitionId) {
        return Result.success("获取成功", deploymentService.getFlowXml(processDefinitionId));
    }
}
