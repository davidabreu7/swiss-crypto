package com.swisspost.swisscrypto.config;

import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean(name = "priceUpdateExecutor")
    public Executor priceUpdateExecutor() {
        return new SimpleAsyncTaskExecutorBuilder()
            .concurrencyLimit(3)
            .threadNamePrefix("price-update-")
            .build();
    }
}
