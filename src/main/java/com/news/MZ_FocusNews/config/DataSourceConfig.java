package com.news.MZ_FocusNews.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

// news와 users 설정
@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "newsDataSourceProperties")
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties newsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "newsDataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource newsDataSource() {
        return newsDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "newsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean newsEntityManagerFactory(
            @Qualifier("newsDataSource") DataSource newsDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(newsDataSource);
        em.setPackagesToScan("com.news.MZ_FocusNews.NewsTable");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean(name = "newsTransactionManager")
    public PlatformTransactionManager newsTransactionManager(
            @Qualifier("newsEntityManagerFactory") LocalContainerEntityManagerFactoryBean newsEntityManagerFactory) {
        return new JpaTransactionManager(newsEntityManagerFactory.getObject());
    }

    @Bean(name = "usersDataSourceProperties")
    @ConfigurationProperties("users.datasource")
    public DataSourceProperties usersDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "usersDataSource")
    @ConfigurationProperties("users.datasource")
    public DataSource usersDataSource() {
        return usersDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "usersEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean usersEntityManagerFactory(
            @Qualifier("usersDataSource") DataSource usersDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(usersDataSource);
        em.setPackagesToScan("com.news.MZ_FocusNews.UsersTable");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "usersTransactionManager")
    public PlatformTransactionManager usersTransactionManager(
            @Qualifier("usersEntityManagerFactory") LocalContainerEntityManagerFactoryBean usersEntityManagerFactory) {
        return new JpaTransactionManager(usersEntityManagerFactory.getObject());
    }
}
