package com.demo.autopayment.business.service;

import com.demo.autopayment.model.entity.LoanApplication;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class UtilService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PlatformTransactionManager transactionManager;

    public UtilService(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Transactional
    public void saveBatch(List<String> lineBatch) {
        int inserted = 0;

        for (String line : lineBatch) {
            LoanApplication loanApplication = ImportDataService.toApplication(line);
            if (Objects.nonNull(loanApplication)) {
                inserted++;
                entityManager.persist(loanApplication);
            }
        }

        entityManager.flush();
        entityManager.clear();

        log.info("Inserted {} records...", inserted);
    }

    public void saveBatchTransactionally(List<String> lineBatch) {
        AtomicInteger inserted = new AtomicInteger();

        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.execute(status -> {
            for (String line : lineBatch) {
                LoanApplication loan = ImportDataService.toApplication(line);
                if (Objects.nonNull(loan)) {
                    inserted.getAndIncrement();
                    entityManager.persist(loan);
                }
            }

            entityManager.flush();
            entityManager.clear();
            System.out.printf("Inserted "+ inserted + " records...\n");
            return null;
        });


    }

}
