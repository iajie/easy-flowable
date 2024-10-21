package com.easyflowable.core.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @package: {@link com.easyflowable.core.domain.dto}
 * @Date: 2024-10-09-10:18
 * @Description:
 * @Author: MoJie
 */
@Data
public class FlowUserTask {

    /**
     * 任务ID
     */
    private String id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 节点key
     */
    private String key;

    /**
     * 执行人
     */
    private String assignee;

    /**
     * 候选人
     */
    private List<String> candidateUsers;

    /**
     * 候选组
     */
    private List<String> candidateGroups;

    /**
     * 流程自定义属性
     */
    private Map<String, Object> flowCustomProps;
    
}
