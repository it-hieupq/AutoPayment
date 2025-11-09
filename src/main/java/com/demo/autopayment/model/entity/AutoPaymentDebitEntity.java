package com.demo.autopayment.model.entity;

import com.demo.autopayment.model.dto.AutoPaymentBpmTask;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "auto_payment_debit", schema = "loan_business")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AutoPaymentDebitEntity {

    @Id
    private String autoPaymentId;
    private String loanApplicationId;
    private String partnerContractId;
    private String msisdn;
    private String registerAutoPaymentStatus;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String productCode;
    private LocalDate paymentDueDate;
    private String enableAutoPayment;
    private String syncAutoPaymentStatus;
    private LocalDateTime syncAutoPaymentDate;
    private String processDefinitionId;
    private String customerId;
    private String customerAccountNo;
    private LocalDateTime lastProcessedAt;
    
    
    public AutoPaymentBpmTask toAutoPaymentBpmTask() {
        return AutoPaymentBpmTask.builder()
            .autoPaymentId(this.getAutoPaymentId())
            .loanApplicationId(this.getLoanApplicationId())
            .partnerContractId(this.getPartnerContractId())
            .msisdn(this.getMsisdn())
            .registerAutoPaymentStatus(this.getRegisterAutoPaymentStatus())
            .createdDate(this.getCreatedDate())
            .updatedDate(this.getUpdatedDate())
            .productCode(this.getProductCode())
            .paymentDueDate(this.getPaymentDueDate())
            .enableAutoPayment(this.getEnableAutoPayment())
            .syncAutoPaymentStatus(this.getSyncAutoPaymentStatus())
            .syncAutoPaymentDate(this.getSyncAutoPaymentDate())
            .processDefinitionId(this.getProcessDefinitionId())
            .customerId(this.getCustomerId())
            .customerAccountNo(this.getCustomerAccountNo())
            .lastProcessedAt(this.getLastProcessedAt())
            .build();
    }
}