package com.superb.solon.properties;

import com.superb.core.config.EasyFlowableConfig;
import com.superb.core.config.EasyFlowableDataSourceConfig;
import com.superb.core.config.EasyFlowableUiConfig;
import lombok.Data;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @Author: MoJie
 * @Date: 2024-11-23 20:15
 * @Description: easy-flowable配置
 */
@Data
@Configuration
@Inject(value = "${easy-flowable}", autoRefreshed = true)
public class EasyFlowableConfigProperties {

    /** 是否开启easy-flowable， 默认开启 */
    private boolean enable;

    /** 是否启动时打印banner */
    private boolean banner;

    /** 是否使用项目中的数据源 */
    private boolean projectDatasource;

    /** 数据源配置，非必填 */
    private EasyFlowableDataSourceConfig dataSource;

    /** easy-flowable全局配置 */
    private EasyFlowableConfig config;

    /** easy-flowable-ui配置 */
    private EasyFlowableUiConfig ui;

}
