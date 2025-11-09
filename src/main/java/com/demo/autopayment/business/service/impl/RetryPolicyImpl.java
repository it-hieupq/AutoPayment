package com.demo.autopayment.business.service.impl;

import com.demo.autopayment.business.service.RetryPolicy;
import com.demo.autopayment.model.constant.AutoPaymentConstant;
import com.demo.autopayment.model.dto.AutoPaymentBpmTask;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RetryPolicyImpl implements RetryPolicy {
    private final Map<String, Integer> retryMap = new ConcurrentHashMap<>();
    public boolean canExecute(AutoPaymentBpmTask task) {
        return retryMap.getOrDefault(task.getLoanApplicationId(), 0) < AutoPaymentConstant.MAX_RETRY_COUNT;
    }

    public void recordFailure(AutoPaymentBpmTask task) {
        retryMap.merge(task.getLoanApplicationId(), 1, Integer::sum);
    }
}
