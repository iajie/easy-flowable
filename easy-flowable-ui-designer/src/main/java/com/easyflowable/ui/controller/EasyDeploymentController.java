package com.easyflowable.ui.controller;

import com.easyflowable.core.domain.dto.DeploymentProcessDef;
import com.easyflowable.core.domain.dto.FlowUserTask;
import com.easyflowable.core.domain.entity.ActReDeployment;
import com.easyflowable.core.service.EasyDeploymentService;
import com.easyflowable.ui.model.PageParams;
import com.easyflowable.ui.model.Result;
import com.mybatisflex.core.paginate.Page;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.util.List;

/**
 * @package: {@link com.easyflowable.ui.controller}
 * @Date: 2024-10-09-9:18
 * @Description: 流程部署管理
 * @Author: MoJie
 */
@RestController
@RequestMapping("easy-flowable/deployment")
public class EasyDeploymentController {

    @Autowired
    private EasyDeploymentService deploymentService;

    /**
     * 流程部署定义分页查询
     * @param pageParams 分页参数
     * @return {@link Page<DeploymentProcessDef>}
     * @Author: MoJie
     * @Date: 2024-10-09 16:15:19
     */
    @PostMapping("page")
    public Result<Page<DeploymentProcessDef>> page(@RequestBody PageParams<ActReDeployment> pageParams) {
        ActReDeployment params = pageParams.getParams();
        Page<ActReDeployment> page = pageParams.getPage();
        return Result.success(deploymentService.page(page.getPageNumber(), page.getPageSize(), params));
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

    /**
     * 流程部署图片
     * @param processDefinitionId 流程定义id
     * @Author: MoJie
     * @Date: 2024-10-09 16:21:50
     */
    @SneakyThrows
    @GetMapping(value = "deploymentImage/{processDefinitionId}")
    public void deploymentImage(@PathVariable String processDefinitionId, HttpServletResponse response) {
        InputStream inputStream = deploymentService.getFlowImage(processDefinitionId);
        response.setContentType("image/png");
        ImageIO.write(ImageIO.read(inputStream), "png", response.getOutputStream());
    }
}
