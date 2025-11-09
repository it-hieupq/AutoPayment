package com.demo.autopayment.configuration;

import com.demo.autopayment.business.service.AutoPaymentBpmProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AutoPaymentStartup implements ApplicationListener<ContextRefreshedEvent> {

    private AutoPaymentBpmProcessor autoPaymentBpmProcessor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("AutoPaymentStartup: onApplicationEvent");
    }
}
