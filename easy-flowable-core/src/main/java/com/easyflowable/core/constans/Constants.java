package com.easyflowable.core.constans;

/**
 * @package: {@link com.easyflowable.core.constans}
 * @Date: 2024-10-25-9:19
 * @Description:
 * @Author: MoJie
 */
public class Constants {

    public static final String[] DEPLOYMENT_COLUMNS = {
            "rd.ID_ AS id", "rp.ID_ AS processDefinitionId", "rd.NAME_ AS name",
            "rp.HAS_START_FORM_KEY_ AS hasStartFormKey", "rd.DEPLOY_TIME_ AS deploymentTime", "rd.KEY_ AS `key`",
            "rd.CATEGORY_ AS modelType", "rd.TENANT_ID_ AS tenantId", "rp.VERSION_ AS version",
            "rp.SUSPENSION_STATE_ AS suspensionState"
    };

}
