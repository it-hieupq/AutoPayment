package com.demo.autopayment.model.constant;

public class AutoPaymentConstant {
    public static final String AUTO_PAYMENT_PROCESS_POOL = "AUTO_PAYMENT_PROCESS_POOL";

    public static final int MAX_REQUEUE_THREAD_SIZE = 5;
    public static final int MAX_RETRY_COUNT = 5;
    public static final int MAX_POOL_SIZE = 15;
    public static final int BATCH_SIZE = 600;
    public static final long FIXED_DELAY_MS = 500L;

    private AutoPaymentConstant() {}
}
