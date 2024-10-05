package com.superb.easyflowable.starter.config;

import com.superb.easyflowable.core.config.EasyFlowableConfig;
import com.superb.easyflowable.core.config.EasyFlowableDataSourceConfig;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @package: {@link com.superb.easyflowable.starter.config}
 * @Date: 2024-09-26-12:51
 * @Description: easy-flowable配置
 * @Author: MoJie
 */
@Data
@ConfigurationProperties("easy-flowable")
@ConditionalOnProperty(prefix = "easy-flowable", name = "enable", havingValue = "true", matchIfMissing = true)
public class EasyFlowableConfigProperties {

    /** 是否开启easy-flowable， 默认开启 */
    private boolean enable = true;

    /** 是否启动时打印banner */
    private boolean banner = true;

    /** 是否使用项目中的数据源 */
    private boolean projectDatasource = true;

    /** 数据源配置，非必填 */
    @NestedConfigurationProperty
    private EasyFlowableDataSourceConfig dataSource = new EasyFlowableDataSourceConfig();

    /** easy-flowable全局配置 */
    @NestedConfigurationProperty
    private EasyFlowableConfig config = new EasyFlowableConfig();

}
