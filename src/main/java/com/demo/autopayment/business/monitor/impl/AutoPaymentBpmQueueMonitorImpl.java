package com.demo.autopayment.business.monitor.impl;

import com.demo.autopayment.business.monitor.AutoPaymentBpmQueueMonitor;
import com.demo.autopayment.model.dto.AutoPaymentBpmTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class AutoPaymentBpmQueueMonitorImpl implements AutoPaymentBpmQueueMonitor {
    private final AtomicInteger enqueued = new AtomicInteger();
    private final AtomicInteger dequeued = new AtomicInteger();

    @Override
    public void incrementEnqueuedCount() {
        enqueued.incrementAndGet();
    }

    @Override
    public void incrementDequeuedCount() {
        dequeued.incrementAndGet();
    }

    @Override
    public void logPoolFull() {
        log.warn("Redis pool full.");
    }

    @Override
    public void logPhoneInProcessingProgress() {
        log.warn("Another loan of this phone is already in processing progress.");
    }

    @Override
    public void logExceededRetry(AutoPaymentBpmTask task) {
        log.error("Max retry exceeded for loanId: {}", task.getLoanApplicationId());
    }

    @Override
    public void logStart(AutoPaymentBpmTask task) {
        System.out.printf("[Start] Loan %s for phone %s%n", task.getLoanApplicationId(), task.getMsisdn());
    }
    @Override
    public void logRetry(AutoPaymentBpmTask task) {
        System.out.printf("[Retry] Loan %s for phone %s%n", task.getLoanApplicationId(), task.getMsisdn());
    }
    @Override
    public void logSuccess(AutoPaymentBpmTask task) {
        System.out.printf("[Success] Loan %s for phone %s%n", task.getLoanApplicationId(), task.getMsisdn());
    }
}
