package com.demo.autopayment.business.repository;

import com.demo.autopayment.model.entity.AutoPaymentDebitEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoPaymentDebitRepo extends JpaRepository<AutoPaymentDebitEntity, Long> {

    @Query("""
        SELECT a FROM AutoPaymentDebitEntity a
        WHERE a.lastProcessedAt IS NULL OR DATE(a.lastProcessedAt) < CURRENT_DATE
        ORDER BY a.createdDate ASC
    """)
    List<AutoPaymentDebitEntity> findUnprocessedTask(Pageable pageable);

    AutoPaymentDebitEntity findFirstByLoanApplicationIdAndMsisdn(String loanApplicationId, String msisdn);
}
