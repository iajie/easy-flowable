package com.superb.core.service;

import com.superb.core.domain.dto.FlowUserTask;
import com.superb.core.domain.dto.Page;
import com.superb.core.domain.entity.DeploymentProcessDef;

import java.io.InputStream;
import java.util.List;

/**
 * {@link com.superb.core.service}
 * @since 1.0  2024-10-09-10:15
 * @author MoJie
 */
public interface EasyDeploymentService {

    /**
     * @param current 页码
     * @param size 页大小
     * @param params 查询参数
     * @return {@link Page} {@link DeploymentProcessDef} 分页查询
     * @author MoJie
     * @since 1.0  2024-10-24 17:42
     */
    Page<DeploymentProcessDef> page(int current, int size, DeploymentProcessDef params);

    /**
     * 通过模型id部署流程
     * @param modelId 模型ID
     * @return {@link String}
     * @author MoJie
     * @since 1.0  2024-10-09 10:20:15
     */
    String deploymentModel(String modelId);

    /**
     * 删除流程实例
     * @param deploymentId 部署ID
     * @param cascade 是否级联删除
     * @author MoJie
     * @since 1.0  2024-10-09 10:21:12
     */
    void deleteDeployment(String deploymentId, Boolean cascade);

    /**
     * 设置流程状态，如果激活就终止，如果终止就激活
     * @param processDefinitionId 流程定义ID
     * @return {@link String}
     * @author MoJie
     * @since 1.0  2024-10-09 10:21:39
     */
    String deploymentState(String processDefinitionId);

    /**
     * 获取用户任务列表
     * @param flowKey 流程定义标识
     * @return {@link List<FlowUserTask>}
     * @author MoJie
     * @since 1.0  2024-10-09 10:22:09
     */
    List<FlowUserTask> getAllFlowUserTask(String flowKey);

    /**
     * @param processDefinitionId 流程定义ID
     * @return {@link InputStream} 获取流程部署图片
     * @author MoJie
     * @since 1.0  2024/10/26 16:19
     */
    InputStream getFlowImage(String processDefinitionId);

    /**
     * @param processDefinitionId 流程定义ID
     * @return {@link InputStream} 获取流程部署XML
     * @author MoJie
     * @since 1.0  2024/11/3 14:23
     */
    String getFlowXml(String processDefinitionId);
}
