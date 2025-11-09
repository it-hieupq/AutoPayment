package com.demo.autopayment.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static com.demo.autopayment.model.constant.AutoPaymentConstant.MAX_POOL_SIZE;

@Configuration
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("autopay-");
        scheduler.setPoolSize(MAX_POOL_SIZE);
        scheduler.initialize();
        return scheduler;
    }
}