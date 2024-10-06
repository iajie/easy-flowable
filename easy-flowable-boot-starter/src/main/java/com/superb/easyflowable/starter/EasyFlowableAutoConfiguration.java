package com.superb.easyflowable.starter;

import com.superb.easyflowable.core.config.EasyFlowableConfig;
import com.superb.easyflowable.core.config.EasyFlowableDataSourceConfig;
import com.superb.easyflowable.core.enums.HistoryLevelEnum;
import com.superb.easyflowable.core.exception.EasyFlowableException;
import com.superb.easyflowable.core.utils.StringUtils;
import com.superb.easyflowable.starter.config.EasyFlowableConfigProperties;
import liquibase.integration.spring.SpringLiquibase;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @package: {@link com.superb.easyflowable.starter}
 * @Date: 2024-09-26-13:00
 * @Description:
 * @Author: MoJie
 */
@Configuration
@EnableConfigurationProperties(value = {EasyFlowableConfigProperties.class})
@ConditionalOnProperty(prefix = "easy-flowable", name = "enable", havingValue = "true", matchIfMissing = true)
public class EasyFlowableAutoConfiguration {

    private final static String CHANGE_LOG = "classpath:/changelog/changelog-master.yaml";
    @Autowired(required = false)
    private DataSource dataSource;
    @Autowired
    private EasyFlowableConfigProperties properties;

    @Bean
    public ProcessEngine processEngine() {
        // 打印banner
        this.printBanner(properties.isBanner());
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        // 数据源配置
        if (properties.isProjectDatasource() && dataSource != null) {
            engineConfiguration.setDataSource(dataSource);
        } else {
            EasyFlowableDataSourceConfig jdbc = properties.getDataSource();
            if (StringUtils.isBlank(jdbc.getDriver())) {
                throw new EasyFlowableException("请配置数据库驱动");
            }
            if (StringUtils.isBlank(jdbc.getUrl())) {
                throw new EasyFlowableException("请配置数据库连接地址");
            }
            if (StringUtils.isBlank(jdbc.getUsername())) {
                throw new EasyFlowableException("请配置数据库用户名");
            }
            if (StringUtils.isBlank(jdbc.getPassword())) {
                throw new EasyFlowableException("请配置数据库密码");
            }
            engineConfiguration.setJdbcDriver(jdbc.getDriver());
            engineConfiguration.setJdbcUrl(jdbc.getUrl());
            engineConfiguration.setJdbcUsername(jdbc.getUsername());
            engineConfiguration.setJdbcPassword(jdbc.getPassword());
            engineConfiguration.setJdbcMaxCheckoutTime(jdbc.getMaxCheckoutTime());
            engineConfiguration.setJdbcMaxActiveConnections(jdbc.getMaxActiveConnections());
            engineConfiguration.setJdbcMaxIdleConnections(jdbc.getMaxIdleConnections());
            engineConfiguration.setJdbcMaxWaitTime(jdbc.getMaxWaitTime());
        }
        // 配置流程引擎
        EasyFlowableConfig flowableConfig = properties.getConfig();
        // 数据库表策略
        engineConfiguration.setDatabaseSchemaUpdate(flowableConfig.isTableSchema() ?
                AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE : AbstractEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
        // 设置流程历史级别
        engineConfiguration.setHistoryLevel(HistoryLevelEnum.getKey(flowableConfig.getHistoryLevel()));
        // 定时任务开关
        engineConfiguration.setAsyncExecutorActivate(flowableConfig.isAsyncExecutorActivate());
        // 邮件发送
        if (flowableConfig.isMail()) {
            engineConfiguration.setMailServers(flowableConfig.getMailConfig());
        }
        return engineConfiguration.buildProcessEngine();
    }

    /**
     * @param isBanner 是否打印banner
     * @Author: MoJie
     * @Date: 2024/10/6 11:24
     * @Description: 打印banner
     */
    private void printBanner(boolean isBanner) {
        if (isBanner) {
            System.out.println("""
                        ______                       ________                    __    __  \s
                       / ____/___ ________  __      / ____/ /___ _      ______ _/ /_  / /__\s
                      / __/ / __ `/ ___/ / / /_____/ /_  / / __ \\ | /| / / __ `/ __ \\/ / _ \\
                     / /___/ /_/ (__  ) /_/ /_____/ __/ / / /_/ / |/ |/ / /_/ / /_/ / /  __/
                    /_____/\\__,_/____/\\__, /     /_/   /_/\\____/|__/|__/\\__,_/_.___/_/\\___/\s
                                     /____/                                                \s
                                        |----version: 1.0.0 ----|\s
                    """);
        }
    }

    @Bean
    public SpringLiquibase liquibase() {
        if (dataSource == null) {
            EasyFlowableDataSourceConfig source = properties.getDataSource();
            dataSource = new DriverManagerDataSource(source.getUrl(), source.getUsername(), source.getPassword());
        }
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(CHANGE_LOG);
        liquibase.setDataSource(dataSource);
        liquibase.setShouldRun(true);
        return liquibase;
    }

}
