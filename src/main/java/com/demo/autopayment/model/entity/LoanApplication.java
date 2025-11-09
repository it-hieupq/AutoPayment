package com.demo.autopayment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String loanId;
    String loanProduct;
    String loanPackage;
    Integer loanAmount;
    String loanStatus;
    String phoneNumber;
    String customerName;
    String identityNumber;
    LocalDate dueDate;
    LocalDateTime createdAt;
    LocalDateTime lastModifiedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }

}
