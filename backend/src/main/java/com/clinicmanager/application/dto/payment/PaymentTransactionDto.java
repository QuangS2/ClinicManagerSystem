package com.clinicmanager.application.dto.payment;

import com.clinicmanager.domain.model.payment.PaymentMethod;
import com.clinicmanager.domain.model.payment.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionDto {
    private UUID id;
    private UUID invoiceId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private LocalDateTime transactionTime;
    private String transferCode;
    private TransactionStatus status;
}
