package com.dustincode.authentication.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories({"com.dustincode.authentication.repository"})
@EntityScan("com.dustincode.authentication.entity")
public class DatabaseConfiguration {
}
