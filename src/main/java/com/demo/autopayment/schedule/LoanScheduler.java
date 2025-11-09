package com.demo.autopayment.schedule;

import com.demo.autopayment.business.monitor.AutoPaymentBpmQueueMonitor;
import com.demo.autopayment.business.repository.AutoPaymentDebitRepo;
import com.demo.autopayment.business.service.AutoPaymentBpmQueueService;
import com.demo.autopayment.model.constant.AutoPaymentConstant;
import com.demo.autopayment.model.entity.AutoPaymentDebitEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class LoanScheduler {

    private final AutoPaymentDebitRepo loanRepository;
    private final AutoPaymentBpmQueueService queueService;
    private final AutoPaymentBpmQueueMonitor monitor;

    /**
     * Scheduled job: chạy từ 07:00 đến 22:00 mỗi 10 phút
     */
//    @Scheduled(cron = "0 0/2 * * * ?")
    public void schedule() {
        try {
            Pageable limit = PageRequest.of(0, AutoPaymentConstant.BATCH_SIZE);
            List<AutoPaymentDebitEntity> tasks = loanRepository.findUnprocessedTask(limit);

            if (tasks.isEmpty()) {
                log.info("[LoanScheduler] No tasks to enqueue.");
                return;
            }

            log.info("[LoanScheduler] Enqueuing {} tasks", tasks.size());

            for (AutoPaymentDebitEntity task : tasks) {
                queueService.enqueue(task.toAutoPaymentBpmTask());
                monitor.incrementEnqueuedCount();
            }

            log.info("[LoanScheduler] Enqueue completed.");
        } catch (Exception e) {
            log.error("[LoanScheduler] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}