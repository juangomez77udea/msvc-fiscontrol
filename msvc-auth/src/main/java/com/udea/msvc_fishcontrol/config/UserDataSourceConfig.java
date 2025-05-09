package com.udea.msvc_fishcontrol.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.udea.msvc_fishcontrol.repositories")
@EntityScan(basePackages = "com.udea.msvc_fishcontrol.models")
public class UserDataSourceConfig {
}