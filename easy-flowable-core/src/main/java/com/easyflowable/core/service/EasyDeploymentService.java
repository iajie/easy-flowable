package com.easyflowable.core.service;

import com.easyflowable.core.constans.Constants;
import com.easyflowable.core.domain.dto.DeploymentProcessDef;
import com.easyflowable.core.domain.dto.FlowUserTask;
import com.easyflowable.core.domain.entity.ActReDeployment;
import com.easyflowable.core.domain.entity.ActReProcessDef;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;

import java.io.InputStream;
import java.util.List;

/**
 * @package: {@link com.easyflowable.core.service}
 * @Date: 2024-10-09-10:15
 * @Description:
 * @Author: MoJie
 */
public interface EasyDeploymentService {

    /**
     * @return {@link QueryChain} {@link ActReDeployment}
     * @Author: MoJie
     * @Date: 2024-10-24 17:45
     * @Description: 使用Mybatis-Flex带有的链式查询
     */
    QueryChain<ActReDeployment> queryChain();

    /**
     * @param current 页码
     * @param size 页大小
     * @param params 查询参数
     * @return {@link Page} {@link DeploymentProcessDef}
     * @Author: MoJie
     * @Date: 2024-10-24 17:42
     * @Description: 分页查询
     */
    default Page<DeploymentProcessDef> page(long current, long size, ActReDeployment params) {
        return this.queryChain()
                .select(Constants.DEPLOYMENT_COLUMNS)
                .from(ActReDeployment.class).as("rd")
                .like(ActReDeployment::getName, params.getName())
                .like(ActReDeployment::getFlowKey, params.getFlowKey())
                .eq(ActReDeployment::getModelType, params.getModelType())
                .eq(ActReDeployment::getTenantId, params.getTenantId())
                .innerJoin(ActReProcessDef.class).as("rp").on(ActReDeployment::getId, ActReProcessDef::getDeploymentId)
                .orderBy(ActReDeployment::getDeploymentTime).desc().pageAs(new Page<>(current, size), DeploymentProcessDef.class);
    }

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

    /**
     * @param processDefinitionId 流程定义ID
     * @Return: {@link InputStream}
     * @Author: MoJie
     * @Date: 2024/10/26 16:19
     * @Description: 获取流程部署图片
     */
    InputStream getFlowImage(String processDefinitionId);
}
