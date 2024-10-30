package com.easyflowable.core.constans;

/**
 * @package: {@link com.easyflowable.core.constans}
 * @Date: 2024-10-25-9:19
 * @Description:
 * @Author: MoJie
 */
public class Constants {

    public static final String EASY_FLOWABLE = "easy-flowable";

    public static final String[] DEPLOYMENT_COLUMNS = {
            "rd.ID_ AS id", "rp.ID_ AS processDefinitionId", "rd.NAME_ AS name",
            "rp.HAS_START_FORM_KEY_ AS hasStartFormKey", "rd.DEPLOY_TIME_ AS deploymentTime", "rd.KEY_ AS `key`",
            "rd.CATEGORY_ AS modelType", "rd.TENANT_ID_ AS tenantId", "rp.VERSION_ AS version",
            "rp.SUSPENSION_STATE_ AS suspensionState"
    };

    public final static String CHANGE_LOG = "classpath:/changelog/changelog-master.yaml";

    public final static String SEQUENCE_FLOW = "sequenceFlow";
    public final static String GATEWAY = "Gateway";
    public final static String EVENT = "Event";

    public final static String INITIATOR = "initiator";

    public final static String FILE = "file";

    /**
     * @param name 流程名称
     * @Return: {@link String}
     * @Author: MoJie
     * @Date: 2024/10/26 17:07
     * @Description: 流程定义
     */
    public static String BPMN20_XML(final String name) {
        return EASY_FLOWABLE + "-" + name + "-bpmn20.xml";
    }

    /**
     * @param name 流程名称
     * @param key 流程标识
     * @Return: {@link String}
     * @Author: MoJie
     * @Date: 2024/10/26 17:07
     * @Description: 流程图
     */
    public static String DIAGRAM_PNG(final String name, final String key) {
        return EASY_FLOWABLE + "-" + name + "-" + key + ".png";
    }
}
