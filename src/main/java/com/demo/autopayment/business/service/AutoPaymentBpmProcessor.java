package com.demo.autopayment.business.service;

import java.util.Map;

public interface AutoPaymentBpmProcessor {
    void startProcessing();
    boolean isPoolFull();
    boolean isPhoneInProgress(String phone);
    Map<String, String> getAutoPaymentProcessPool();
    void addToProcessPool(String phone, String loanApplicationId);
    void removeFromProcessPool(String phone);
    void tryDequeueAndProcessOnce();
}