package com.superb.starter;

import com.superb.core.config.EasyFlowableConfig;
import com.superb.core.config.EasyFlowableDataSourceConfig;
import com.superb.core.constans.Constants;
import com.superb.core.domain.enums.HistoryLevelEnum;
import com.superb.core.exception.EasyFlowableException;
import com.superb.core.service.*;
import com.superb.core.utils.EasyFlowableStringUtils;
import com.superb.starter.api.*;
import com.superb.starter.config.DataSourceProperties;
import com.superb.starter.config.EasyFlowableConfigProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.*;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @package: {@link com.superb.starter}
 * @Date: 2024-09-26-13:00
 * @Description:
 * @Author: MoJie
 */
@Configuration
@ConditionalOnProperty(prefix = "easy-flowable", name = "enable", havingValue = "true", matchIfMissing = true)
public class EasyFlowableAutoConfiguration {

    @Resource
    private EasyFlowableConfigProperties properties;
    @Resource
    private DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource easyFlowableDatasource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.type(HikariDataSource.class);
        if (!properties.isProjectDatasource()) {
            EasyFlowableDataSourceConfig jdbc = properties.getDataSource();
            this.checkJdbc(jdbc);
            dataSourceBuilder.url(jdbc.getUrl())
                    .username(jdbc.getUsername())
                    .driverClassName(jdbc.getDriver())
                    .password(jdbc.getPassword());
        } else {
            if (this.dataSourceProperties == null) {
                throw new EasyFlowableException("如果您不想使用项目中的数据源，" +
                        "请设置easy-flowable.project-datasource为false, 否则请配置spring.datasource");
            }
            dataSourceBuilder
                    .driverClassName(dataSourceProperties.getDriverClassName())
                    .url(dataSourceProperties.getUrl())
                    .username(dataSourceProperties.getUsername())
                    .password(dataSourceProperties.getPassword());
        }
        return dataSourceBuilder.build();
    }

    private void checkJdbc(EasyFlowableDataSourceConfig jdbc) {
        if (EasyFlowableStringUtils.isBlank(jdbc.getDriver())) {
            throw new EasyFlowableException("请配置数据库驱动");
        }
        if (EasyFlowableStringUtils.isBlank(jdbc.getUrl())) {
            throw new EasyFlowableException("请配置数据库连接地址");
        }
        if (EasyFlowableStringUtils.isBlank(jdbc.getUsername())) {
            throw new EasyFlowableException("请配置数据库用户名");
        }
        if (EasyFlowableStringUtils.isBlank(jdbc.getPassword())) {
            throw new EasyFlowableException("请配置数据库密码");
        }
    }

    @Bean
    public ProcessEngine processEngine(DataSource dataSource) {
        // 打印banner
        this.printBanner(properties.isBanner());
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        // 数据源配置
        if (properties.isProjectDatasource()) {
            engineConfiguration.setDataSource(dataSource);
        } else {
            EasyFlowableDataSourceConfig jdbc = properties.getDataSource();
            this.checkJdbc(jdbc);
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
//        if (flowableConfig.isMail()) {
//            engineConfiguration.setMailServers(flowableConfig.getMailConfig());
//        }
        return engineConfiguration.buildProcessEngine();
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    public ProcessDiagramGenerator processDiagramGenerator(ProcessEngine processEngine) {
        return processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
    }

    /**
     * @param isBanner 是否打印banner
     * @Author: MoJie
     * @Date: 2024/10/6 11:24
     * @Description: 打印banner
     */
    private void printBanner(boolean isBanner) {
        if (isBanner) {
            System.out.println(Constants.BANNER);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public EasyUserService easyUserService() {
        return new DefaultEasyUserServiceImpl(properties.getUi());
    }

    @Bean
    public EasyModelService easyModelService() {
        return new EasyModelServiceImpl();
    }

    @Bean
    @ConditionalOnClass({RepositoryService.class, RuntimeService.class})
    public EasyDeploymentService easyFlowDeploymentService() {
        return new EasyDeploymentServiceImpl();
    }

    @Bean
    @ConditionalOnClass({RepositoryService.class, RuntimeService.class, TaskService.class, HistoryService.class})
    public EasyProcessInstanceService easyFlowProcessInstanceService() {
        return new EasyProcessInstanceServiceImpl();
    }

    @Bean
    @ConditionalOnClass({RuntimeService.class, TaskService.class, HistoryService.class})
    public EasyTaskService easyFlowTaskService() {
        return new EasyTaskServiceImpl();
    }

}