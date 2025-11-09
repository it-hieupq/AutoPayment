package com.demo.autopayment.business.service;

import com.demo.autopayment.model.dto.AutoPaymentBpmTask;

public interface AutoPaymentBpmQueueService {
    int getQueueSize();
    void enqueue(AutoPaymentBpmTask task);
    AutoPaymentBpmTask dequeue() throws InterruptedException;
    AutoPaymentBpmTask tryDequeue();
    void requeueWithDelay(AutoPaymentBpmTask task, long delayMillis);
}
