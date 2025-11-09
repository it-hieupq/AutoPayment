package com.demo.autopayment.business.service.impl;

import com.demo.autopayment.business.service.AutoPaymentBpmQueueService;
import com.demo.autopayment.model.constant.AutoPaymentConstant;
import com.demo.autopayment.model.dto.AutoPaymentBpmTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
@Slf4j
public class AutoPaymentBpmQueueServiceImpl implements AutoPaymentBpmQueueService {
    private final BlockingQueue<AutoPaymentBpmTask> queue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(AutoPaymentConstant.MAX_REQUEUE_THREAD_SIZE);

    public void enqueue(AutoPaymentBpmTask task) {
        log.info("Queue size = {}. Enqueuing task: {}", getQueueSize() ,task.getLoanApplicationId());
        queue.offer(task); // none-blocking
    }

    public AutoPaymentBpmTask dequeue() throws InterruptedException {
        log.info("Queue size = {}. dequeue task...", getQueueSize());
        return queue.take(); // get, blocking
    }

    @Override
    public AutoPaymentBpmTask tryDequeue() {
        log.info("Queue size = {}. tryDequeue task...", getQueueSize());
        return queue.poll(); //get, non-blocking
    }

    @Override
    public void requeueWithDelay(AutoPaymentBpmTask task, long delayMillis) {
        log.info("Queue size = {}. Requeuing task {} with delay: {} ms", getQueueSize(), task.getLoanApplicationId(), delayMillis);
        scheduler.schedule(() -> enqueue(task), delayMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }
}