package com.demo.autopayment.business.service.impl;

import com.demo.autopayment.business.monitor.AutoPaymentBpmQueueMonitor;
import com.demo.autopayment.business.service.AutoPaymentBpmProcessor;
import com.demo.autopayment.business.service.AutoPaymentBpmQueueService;
import com.demo.autopayment.business.service.RedisService;
import com.demo.autopayment.business.service.RetryPolicy;
import com.demo.autopayment.model.constant.AutoPaymentConstant;
import com.demo.autopayment.model.dto.AutoPaymentBpmTask;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoPaymentBpmProcessorImpl implements AutoPaymentBpmProcessor {
    private final RedisService redisService;
    private final AutoPaymentBpmQueueService queueService;
    private final RetryPolicy retryPolicy;
    private final AutoPaymentBpmQueueMonitor monitor;

    @Override
    public void startProcessing() {
        log.info("Starting AutoPaymentBpmProcessor...");

        Executors.newSingleThreadExecutor().execute(() -> {
            while (true) {
                try {

                    if (isPoolFull()) {
                        log.warn("Redis pool full. Sleep 500ms");
                        Thread.sleep(1000);
                        continue;
                    }

                    AutoPaymentBpmTask task = queueService.dequeue();
                    monitor.incrementDequeuedCount();

                    if (isPhoneInProgress(task.getMsisdn())) {
                        monitor.logPhoneInProcessingProgress();
                        queueService.requeueWithDelay(task, 100);
                        continue;
                    }

                    addToProcessPool(task.getMsisdn(), task.getLoanApplicationId());

                    if (!retryPolicy.canExecute(task)) {
                        monitor.logExceededRetry(task);
                        removeFromProcessPool(task.getMsisdn());
                        continue;
                    }

                   startCamundaProcess(task.getLoanApplicationId());


                } catch (Exception e) {
                    log.error("Error processing task: {}", e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void tryDequeueAndProcessOnce() {
        if (isPoolFull()) {
            log.info("Pool is full");
            return;
        }

        AutoPaymentBpmTask task = queueService.tryDequeue();
        if (task == null) {
            log.info("Queue is empty. No task to process.");
            return;
        }

        if (isPhoneInProgress(task.getMsisdn())) {
            log.info("Another loan of this phone is already in processing progress.");
            queueService.requeueWithDelay(task, 100);
            return;
        }

        addToProcessPool(task.getMsisdn(), task.getLoanApplicationId());

        if (!retryPolicy.canExecute(task)) {
            log.info("Max retry exceeded for loanId: {}", task.getLoanApplicationId());
            removeFromProcessPool(task.getMsisdn());
            return;
        }

        boolean started = startCamundaProcess(task.getLoanApplicationId());

        if (!started) {
            log.info("Loan {} failed to start. Retrying enqueue...", task.getLoanApplicationId());
            retryPolicy.recordFailure(task);
            removeFromProcessPool(task.getMsisdn());
            queueService.requeueWithDelay(task, 100);
        } else {
            log.info("Loan {} started successfully.", task.getLoanApplicationId());
            monitor.logStart(task);
        }
    }

    @Override
    public boolean isPoolFull() {
        Map<String, String> bpmPool = getAutoPaymentProcessPool();
        return Objects.nonNull(bpmPool) && bpmPool.size() >= AutoPaymentConstant.MAX_POOL_SIZE;
    }

    @Override
    public boolean isPhoneInProgress(String phone) {
        Map<String, String> bpmPool = getAutoPaymentProcessPool();
        return Objects.nonNull(bpmPool) && bpmPool.containsKey(phone);
    }

    @Override
    public Map<String, String> getAutoPaymentProcessPool() {
        Map<String, String> bpmPool = redisService.get(AutoPaymentConstant.AUTO_PAYMENT_PROCESS_POOL, new TypeReference<>() {});
        if (Objects.isNull(bpmPool)) {
            bpmPool = new HashMap<>();
        }
        log.info("Size = {} ; Process Pool: {}. ", bpmPool.size(), bpmPool);
        return bpmPool;
    }

    @Override
    public void addToProcessPool(String phone, String loanApplicationId) {
        Map<String, String> bpmPool = getAutoPaymentProcessPool();
        bpmPool.put(phone, loanApplicationId);
        redisService.set(AutoPaymentConstant.AUTO_PAYMENT_PROCESS_POOL, bpmPool);
    }

    @Override
    public void removeFromProcessPool(String phone) {
        Map<String, String> bpmPool = getAutoPaymentProcessPool();
        bpmPool.remove(phone);
        redisService.set(AutoPaymentConstant.AUTO_PAYMENT_PROCESS_POOL, bpmPool);
    }

    private boolean startCamundaProcess(String loanApplicationId) {
        log.info("Starting Camunda process for loanApplicationId: {}", loanApplicationId);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error while sleeping: {}", e.getMessage(), e);
            return false;
        }
        return true;
    }
}
