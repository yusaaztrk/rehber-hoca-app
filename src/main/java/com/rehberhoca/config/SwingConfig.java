package com.rehberhoca.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.rehberhoca.repository")
@EntityScan(basePackages = "com.rehberhoca.entity")
public class SwingConfig {
    
    // @Bean metodunu SİLİN - RehberHocaAnaEkran zaten @Component
    
}