package com.superb.starter.config;

import com.superb.core.config.EasyFlowableConfig;
import com.superb.core.config.EasyFlowableDataSourceConfig;
import com.superb.core.config.EasyFlowableUiConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * easy-flowable配置
 * @since 1.0  2024-09-26-12:51
 * @author MoJie
 */
@Data
@ConfigurationProperties("easy-flowable")
public class EasyFlowableConfigProperties {

    /** 是否开启easy-flowable， 默认开启 */
    private boolean enable = true;

    /** 是否启动时打印banner */
    private boolean banner = true;

    /** 是否使用项目中的数据源 */
    private boolean projectDatasource = false;

    /** 数据源配置，非必填 */
    @NestedConfigurationProperty
    private EasyFlowableDataSourceConfig dataSource = new EasyFlowableDataSourceConfig();

    /** easy-flowable全局配置 */
    @NestedConfigurationProperty
    private EasyFlowableConfig config = new EasyFlowableConfig();

    /** easy-flowable-ui配置 */
    @NestedConfigurationProperty
    private EasyFlowableUiConfig ui = new EasyFlowableUiConfig();

}
