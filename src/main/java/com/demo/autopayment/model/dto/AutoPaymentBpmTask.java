package com.demo.autopayment.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AutoPaymentBpmTask {
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
}
