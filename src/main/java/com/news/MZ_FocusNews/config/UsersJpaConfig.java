package com.news.MZ_FocusNews.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.news.MZ_FocusNews.UsersTable", // UsersTable 패키지의 JPA 리포지토리 활성화
        entityManagerFactoryRef = "usersEntityManagerFactory", // Users 엔티티 매니저 팩토리 참조
        transactionManagerRef = "usersTransactionManager" // Users 트랜잭션 매니저 참조
)
@EntityScan(basePackages = "com.news.MZ_FocusNews.UsersTable") // UsersTable 패키지의 엔티티 스캔
@EnableTransactionManagement // 트랜잭션 관리 활성화
public class UsersJpaConfig {
}
