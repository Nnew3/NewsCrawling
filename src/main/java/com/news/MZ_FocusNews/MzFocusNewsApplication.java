package com.news.MZ_FocusNews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
		basePackages = "com.news.MZ_FocusNews.NewsTable",
		entityManagerFactoryRef = "newsEntityManagerFactory",
		transactionManagerRef = "newsTransactionManager"
)
public class MzFocusNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MzFocusNewsApplication.class, args);
	}

}
