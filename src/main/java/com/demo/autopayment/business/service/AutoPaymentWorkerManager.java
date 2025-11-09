package com.demo.autopayment.business.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.demo.autopayment.model.constant.AutoPaymentConstant.FIXED_DELAY_MS;
import static com.demo.autopayment.model.constant.AutoPaymentConstant.MAX_POOL_SIZE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoPaymentWorkerManager implements DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private final AutoPaymentBpmProcessor processor;
    private final ThreadPoolTaskScheduler taskScheduler;

    private final ScheduledFuture<?>[] futures = new ScheduledFuture[MAX_POOL_SIZE];
    private final AtomicInteger activeTasks = new AtomicInteger();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        log.info("Starting {} auto-payment workers using ThreadPoolTaskScheduler...", MAX_POOL_SIZE);
//        IntStream.range(0, MAX_POOL_SIZE).forEach(i -> {
//            futures[i] = taskScheduler.scheduleWithFixedDelay(
//                    () -> {
//                        try {
//                            activeTasks.incrementAndGet();
//                            processor.tryDequeueAndProcessOnce();
//                        } catch (Exception e) {
//                            log.error("Worker {} encountered error", i, e);
//                        } finally {
//                            activeTasks.decrementAndGet();
//                        }
//                    },
//                    Instant.now().plusMillis(100),
//                    Duration.ofMillis(FIXED_DELAY_MS)
//            );
//        });
    }

    @Override
    public void destroy() {
        log.info("Shutting down auto-payment workers...");
        for (ScheduledFuture<?> future : futures) {
            if (future != null && !future.isCancelled()) {
                future.cancel(true);
            }
        }
    }

    public int getActiveTaskCount() {
        return activeTasks.get();
    }
}