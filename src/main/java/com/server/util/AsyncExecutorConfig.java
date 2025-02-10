package com.server.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncExecutorConfig {
    @Bean
    public Executor asyncExecutor() {
        return Executors.newFixedThreadPool(20);
    }
}
