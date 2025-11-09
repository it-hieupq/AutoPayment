//package com.demo.autopayment.business.service;
//
//import com.demo.autopayment.business.repository.AutoPaymentDebitRepo;
//import com.demo.autopayment.model.constant.AutoPaymentConstant;
//import com.demo.autopayment.model.entity.AutoPaymentDebitEntity;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class FakeCamundaSimulator {
//
//    private final RedisService redisService;
//    private final AutoPaymentDebitRepo autoPaymentDebitRepo;
//    private final AutoPaymentBpmProcessor autoPaymentBpmProcessor;
//    /**
//     * Mỗi 1s giả lập việc Camunda hoàn tất một process instance
//     */
//    @Scheduled(fixedDelay = 1000)
//    public void simulateCamundaCompletion() {
//        log.info("[FakeCamundaSimulator] Simulating Camunda completion starting...");
//        Map<String, String> processPool = autoPaymentBpmProcessor.getAutoPaymentProcessPool();
//
//        if (processPool == null || processPool.isEmpty()) {
//            log.info("[FakeCamundaSimulator] Pool is empty.");
//            return;
//        }
//
//        processPool.entrySet().stream().parallel().forEach(entry -> {
//            String msisdn = entry.getKey();
//            String loanApplicationId = entry.getValue();
//
//            // Remove entry from pool
//            processPool.remove(msisdn);
//
//            redisService.set(AutoPaymentConstant.AUTO_PAYMENT_PROCESS_POOL, processPool);
//            AutoPaymentDebitEntity autoPaymentDebitEntity = autoPaymentDebitRepo.findFirstByLoanApplicationIdAndMsisdn(loanApplicationId, msisdn);
//            autoPaymentDebitEntity.setLastProcessedAt(LocalDateTime.now());
//            autoPaymentDebitEntity.setUpdatedDate(LocalDateTime.now());
//            autoPaymentDebitRepo.save(autoPaymentDebitEntity);
//
//            log.info("[FakeCamundaSimulator] Removed {}::{}", msisdn, loanApplicationId);
//
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                log.error("[FakeCamundaSimulator] Error while sleeping", e);
//            }
//        });
//
//        log.info("[FakeCamundaSimulator] Simulating Camunda completion finished.");
//    }
//}