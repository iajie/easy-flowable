package com.superb.solon.config;

import com.superb.core.config.EasyFlowableConfig;
import com.superb.core.config.EasyFlowableDataSourceConfig;
import com.superb.core.constans.Constants;
import com.superb.core.domain.enums.HistoryLevelEnum;
import com.superb.core.exception.EasyFlowableException;
import com.superb.core.service.*;
import com.superb.core.utils.EasyFlowableStringUtils;
import com.superb.solon.api.*;
import com.superb.solon.properties.EasyFlowableConfigProperties;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.*;
import org.flowable.image.ProcessDiagramGenerator;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.dynamicds.DynamicDataSource;

import javax.sql.DataSource;

/**
 * @Author: MoJie
 * @Date: 2024-11-23 20:17
 * @Description:
 */
@Configuration
public class EasyFlowableConfiguration {

    @Inject
    private EasyFlowableConfigProperties properties;
    private DataSource dataSource;

    public EasyFlowableConfiguration() {
        Solon.context().getBeanAsync(DataSource.class, bw -> this.dataSource = bw);
    }

    private void checkJdbc(EasyFlowableDataSourceConfig jdbc) {
        if (jdbc == null) {
            throw new EasyFlowableException("请配置数据库相关信息(easy-flowable.data-source)");
        }
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
    @Condition(onProperty = "${easy-flowable.project-datasource} = true", onBean = DataSource.class)
    public ProcessEngine projectDataSourceProcessEngine() {
        return this.processEngine();
    }

    @Bean
    @Condition(onProperty = "${easy-flowable.project-datasource} = false")
    public ProcessEngine processEngine() {
        // 打印banner
        this.printBanner(properties.isBanner());
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        // 数据源配置
        if (properties.isProjectDatasource()) {
            if (dataSource == null) {
                throw new EasyFlowableException("如果您不想使用项目中的数据源，" +
                        "请设置easy-flowable.project-datasource为false, 否则请配置solon.dataSources");
            }
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
    @Condition(onBean = ProcessEngine.class)
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    @Condition(onBean = ProcessEngine.class)
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    @Condition(onBean = ProcessEngine.class)
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    @Condition(onBean = ProcessEngine.class)
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    @Condition(onBean = ProcessEngine.class)
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
    @Condition(onMissingBean = EasyUserService.class)
    public EasyUserService easyUserService() {
        return new DefaultEasyUserServiceImpl(properties.getUi());
    }

    @Bean
    @Condition(onBean = ProcessEngine.class)
    public EasyModelService easyModelService() {
        return new EasyModelServiceImpl();
    }

    @Bean
    @Condition(onBean = ProcessEngine.class)
    public EasyDeploymentService easyFlowDeploymentService() {
        return new EasyDeploymentServiceImpl();
    }

    @Bean
    @Condition(onBean = ProcessEngine.class)
    public EasyProcessInstanceService easyFlowProcessInstanceService() {
        return new EasyProcessInstanceServiceImpl();
    }

    @Bean
    @Condition(onBean = ProcessEngine.class)
    public EasyTaskService easyFlowTaskService() {
        return new EasyTaskServiceImpl();
    }

}
