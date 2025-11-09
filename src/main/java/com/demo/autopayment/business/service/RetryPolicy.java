package com.demo.autopayment.business.service;

import com.demo.autopayment.model.dto.AutoPaymentBpmTask;

public interface RetryPolicy {
    boolean canExecute(AutoPaymentBpmTask task);
    void recordFailure(AutoPaymentBpmTask task);
}
