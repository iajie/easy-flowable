package online.easyflowable.core.service;

import com.mybatisflex.core.service.IService;
import online.easyflowable.core.domain.dto.FlowUserTask;
import online.easyflowable.core.domain.entity.ActReDeployment;

import java.util.List;

/**
 * @package: {@link online.easyflowable.core.service}
 * @Date: 2024-10-09-10:15
 * @Description:
 * @Author: MoJie
 */
public interface EasyFlowDeploymentService extends IService<ActReDeployment> {

    /**
     * 通过模型id部署流程
     * @param modelId 模型ID
     * @return {@link String}
     * @Author: MoJie
     * @Date: 2024-10-09 10:20:15
     */
    String deploymentModel(String modelId);

    /**
     * 删除流程实例
     * @param deploymentId 部署ID
     * @param cascade 是否级联删除
     * @Author: MoJie
     * @Date: 2024-10-09 10:21:12
     */
    void deleteDeployment(String deploymentId, Boolean cascade);

    /**
     * 设置流程状态，如果激活就终止，如果终止就激活
     * @param processDefinitionId 流程定义ID
     * @return {@link String}
     * @Author: MoJie
     * @Date: 2024-10-09 10:21:39
     */
    String deploymentState(String processDefinitionId);

    /**
     * 获取用户任务列表
     * @param flowKey 流程定义标识
     * @return {@link List<FlowUserTask>}
     * @Author: MoJie
     * @Date: 2024-10-09 10:22:09
     */
    List<FlowUserTask> getAllFlowUserTask(String flowKey);
}
