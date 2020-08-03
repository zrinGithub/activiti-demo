package com.zr.activitidemo.config;

import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * Description:
 *
 * @author zhangr
 * 2020/8/3 11:24
 */
@Configuration
public class ActivitiConfig {
    @Value("${activiti.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${activiti.datasource.url}")
    private String url;

    @Value("${activiti.datasource.username}")
    private String username;

    @Value("${activiti.datasource.password}")
    private String password;

    /**
     * 数据源配置
     */
    @Bean
    @SuppressWarnings("unchecked")
    public DataSource activitiDataSource() throws ClassNotFoundException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClass((Class<? extends Driver>) Class.forName(driverClassName));
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /**
     * 事务管理配置
     */
    @Bean
    public PlatformTransactionManager transactionManager() throws ClassNotFoundException {
        return new DataSourceTransactionManager(activitiDataSource());
    }

    /**
     * 流程引擎配置
     */
    @Bean
    public ProcessEngineConfigurationImpl processEngineConfiguration() throws ClassNotFoundException {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        //设置数据源
        configuration.setDataSource(activitiDataSource());
        //创建表的规则 ProcessEngineConfiguration
        //false         即不会创建
        //create-drop   会删除重新创建
        //true          没有表自动创建
        //update
        configuration.setDatabaseSchemaUpdate("update");
        configuration.setTransactionManager(transactionManager());
        configuration.setJobExecutorActivate(false);//activiti5
        configuration.setAsyncExecutorActivate(true); //activiti6
        configuration.setAsyncExecutorEnabled(true);//activiti5
        configuration.setHistory(HistoryLevel.FULL.getKey());

        return configuration;
    }

    /**
     * 这里就是和spring结合的地方了，spring使用FactoryBean生成对应的ProcessEngine
     */
    @Bean
    public ProcessEngineFactoryBean processEngineFactoryBean() throws ClassNotFoundException {
        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }

    /**
     * processEngine
     */
    @Bean
    public ProcessEngine processEngine() {
        try {
            return processEngineFactoryBean().getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public RepositoryService repositoryService() {
        return processEngine().getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService() {
        return processEngine().getRuntimeService();
    }

    @Bean
    public TaskService taskService() {
        return processEngine().getTaskService();
    }

    @Bean
    public HistoryService historyService() {
        return processEngine().getHistoryService();
    }

    @Bean
    public FormService formService() {
        return processEngine().getFormService();
    }

    @Bean
    public IdentityService identityService() {
        return processEngine().getIdentityService();
    }

    @Bean
    public ManagementService managementService() {
        return processEngine().getManagementService();
    }
}
