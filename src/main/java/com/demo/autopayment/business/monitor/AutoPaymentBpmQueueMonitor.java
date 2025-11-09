package com.demo.autopayment.business.monitor;

import com.demo.autopayment.model.dto.AutoPaymentBpmTask;

public interface AutoPaymentBpmQueueMonitor {
    void incrementEnqueuedCount();
    void incrementDequeuedCount();
    void logPoolFull();
    void logPhoneInProcessingProgress();
    void logRetry(AutoPaymentBpmTask task);
    void logExceededRetry(AutoPaymentBpmTask task);
    void logSuccess(AutoPaymentBpmTask task);
    void logStart(AutoPaymentBpmTask task);
}
