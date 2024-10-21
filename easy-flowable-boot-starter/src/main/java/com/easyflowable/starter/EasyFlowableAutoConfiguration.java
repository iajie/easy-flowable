package com.easyflowable.starter;

import com.mybatisflex.core.datasource.DataSourceBuilder;
import com.easyflowable.core.config.EasyFlowableConfig;
import com.easyflowable.core.config.EasyFlowableDataSourceConfig;
import com.easyflowable.core.domain.interfaces.EasyFlowEntityInterface;
import com.easyflowable.core.enums.HistoryLevelEnum;
import com.easyflowable.core.exception.EasyFlowableException;
import com.easyflowable.core.service.EasyFlowDeploymentService;
import com.easyflowable.core.service.EasyFlowProcessInstanceService;
import com.easyflowable.core.service.EasyFlowTaskService;
import com.easyflowable.core.service.EasyModelService;
import com.easyflowable.core.utils.StringUtils;
import com.easyflowable.starter.api.EasyFlowDeploymentServiceImpl;
import com.easyflowable.starter.api.EasyFlowProcessInstanceServiceImpl;
import com.easyflowable.starter.api.EasyFlowTaskServiceImpl;
import com.easyflowable.starter.api.EasyModelServiceImpl;
import com.easyflowable.starter.config.EasyFlowableConfigProperties;
import com.easyflowable.starter.config.EntityInterfaceImpl;
import liquibase.integration.spring.SpringLiquibase;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

/**
 * @package: {@link com.easyflowable.starter}
 * @Date: 2024-09-26-13:00
 * @Description:
 * @Author: MoJie
 */
@Configuration
@EnableConfigurationProperties(value = {EasyFlowableConfigProperties.class})
@ConditionalOnProperty(prefix = "easy-flowable", name = "enable", havingValue = "true", matchIfMissing = true)
public class EasyFlowableAutoConfiguration {

    private final static String CHANGE_LOG = "classpath:/changelog/changelog-master.yaml";
    @Autowired
    private EasyFlowableConfigProperties properties;
    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource easyFlowableDatasource() {
        Map<String, String> map = new HashMap<>();
        map.put("type", "hikari");
        if (!properties.isProjectDatasource()) {
            EasyFlowableDataSourceConfig jdbc = properties.getDataSource();
            map.put("url", jdbc.getUrl());
            map.put("username", jdbc.getUsername());
            map.put("password", jdbc.getPassword());
        } else {
            map.put("url", dataSourceProperties.getUrl());
            map.put("username", dataSourceProperties.getUsername());
            map.put("password", dataSourceProperties.getPassword());
        }
        DataSourceBuilder builder = new DataSourceBuilder(map);
        return builder.build();
    }

    @Bean
    public ProcessEngine processEngine() {
        // 打印banner
        this.printBanner(properties.isBanner());
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        // 数据源配置
        if (properties.isProjectDatasource()) {
            engineConfiguration.setDataSource(this.easyFlowableDatasource());
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

    @Bean
    @Primary
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    @Primary
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    @Primary
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    @Primary
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
            System.out.println("""
                        ______                       ________                    __    __  \s
                       / ____/___ ________  __      / ____/ /___ _      ______ _/ /_  / /__\s
                      / __/ / __ `/ ___/ / / /_____/ /_  / / __ \\ | /| / / __ `/ __ \\/ / _ \\
                     / /___/ /_/ (__  ) /_/ /_____/ __/ / / /_/ / |/ |/ / /_/ / /_/ / /  __/
                    /_____/\\__,_/____/\\__, /     /_/   /_/\\____/|__/|__/\\__,_/_.___/_/\\___/\s
                                     /____/                                                \s
                                        |---- https://www.easy-flowable.online ----|\s
                    """);
        }
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(CHANGE_LOG);
        liquibase.setDataSource(this.easyFlowableDatasource());
        liquibase.setShouldRun(this.properties.getConfig().isTableSchema());
        return liquibase;
    }

    /**
     * 如果没有注册自定义条件器，使用默认
     * @return {@link EasyFlowEntityInterface}
     * @Author: MoJie
     * @Date: 2024-10-09 15:55:36
     */
    @Bean
    @ConditionalOnMissingBean
    public EasyFlowEntityInterface easyFlowEntityInterface() {
        return new EntityInterfaceImpl();
    }

    @Bean
    @Primary
    public EasyModelService easyModelService() {
        return new EasyModelServiceImpl();
    }

    @Bean
    @ConditionalOnClass({RepositoryService.class, RuntimeService.class})
    public EasyFlowDeploymentService easyFlowDeploymentService() {
        return new EasyFlowDeploymentServiceImpl();
    }

    @Bean
    @ConditionalOnClass({RepositoryService.class, RuntimeService.class, TaskService.class, HistoryService.class})
    public EasyFlowProcessInstanceService easyFlowProcessInstanceService() {
        return new EasyFlowProcessInstanceServiceImpl();
    }

    @Bean
    @ConditionalOnClass({RuntimeService.class, TaskService.class, HistoryService.class})
    public EasyFlowTaskService easyFlowTaskService() {
        return new EasyFlowTaskServiceImpl();
    }

}
