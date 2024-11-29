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
 * easy-flowable自动装配
 * @since 1.0  2024-09-26-13:00
 * @author MoJie
 */
@Configuration
@ConditionalOnProperty(prefix = "easy-flowable", name = "enable", havingValue = "true", matchIfMissing = true)
public class EasyFlowableAutoConfiguration {

    @Resource
    private EasyFlowableConfigProperties properties;
    @Resource
    private DataSourceProperties dataSourceProperties;

    /**
     * 数据源设置
     * @return {@link DataSource}
     * @author MoJie
     */
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
            if (this.dataSourceProperties.getUrl() == null) {
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

    /**
     * 校验数据库配置
     * @param jdbc 配置
     * @author MoJie
     */
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

    /**
     * 流程引擎配置到容器中
     * @param dataSource 传入数据源
     * @return {@link ProcessEngine}
     * @author MoJie
     */
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

    /**
     * 构建流程引擎运行任务
     * @param processEngine 流程引擎
     * @return {@link RuntimeService}
     * @author MoJie
     */
    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    /**
     * 流程基础信息设置
     * @param processEngine 流程引擎
     * @return {@link RepositoryService}
     * @author MoJie
     */
    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    /**
     * 构建流程引擎历史执行器
     * @param processEngine 流程引擎
     * @return {@link HistoryService}
     * @author MoJie
     */
    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    /**
     * 流程任务执行器
     * @param processEngine 流程引擎
     * @return {@link TaskService}
     * @author MoJie
     */
    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    /**
     * 流程图实现器
     * @param processEngine 流程引擎
     * @return {@link ProcessDiagramGenerator}
     * @author MoJie
     */
    @Bean
    public ProcessDiagramGenerator processDiagramGenerator(ProcessEngine processEngine) {
        return processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
    }

    /**
     * 打印banner
     * @param isBanner 是否打印banner
     * @author MoJie
     * @since 1.0  2024/10/6 11:24
     */
    private void printBanner(boolean isBanner) {
        if (isBanner) {
            System.out.println(Constants.BANNER);
        }
    }

    /**
     * 配置默认用户信息，如果没有自定义实现就是用默认的实现
     * @return {@link EasyUserService}
     * @author MoJie
     */
    @Bean
    @ConditionalOnMissingBean
    public EasyUserService easyUserService() {
        return new DefaultEasyUserServiceImpl(properties.getUi());
    }

    /**
     * 模型实现方法
     * @return {@link EasyModelService}
     * @author MoJie
     */
    @Bean
    public EasyModelService easyModelService() {
        return new EasyModelServiceImpl();
    }

    /**
     * 实现流程部署方法
     * @return {@link EasyDeploymentService}
     * @author MoJie
     */
    @Bean
    @ConditionalOnClass({RepositoryService.class, RuntimeService.class})
    public EasyDeploymentService easyFlowDeploymentService() {
        return new EasyDeploymentServiceImpl();
    }

    /**
     * 构建流程实例实现
     * @return {@link EasyProcessInstanceService}
     * @author MoJie
     */
    @Bean
    @ConditionalOnClass({RepositoryService.class, RuntimeService.class, TaskService.class, HistoryService.class})
    public EasyProcessInstanceService easyFlowProcessInstanceService() {
        return new EasyProcessInstanceServiceImpl();
    }

    /**
     * 构建任务实现
     * @return {@link EasyTaskService}
     * @author MoJie
     */
    @Bean
    @ConditionalOnClass({RuntimeService.class, TaskService.class, HistoryService.class})
    public EasyTaskService easyFlowTaskService() {
        return new EasyTaskServiceImpl();
    }

}
