package com.clinicmanager.presentation.response.payment;

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
public class PaymentTransactionResponse {
    private UUID id;
    private UUID invoiceId;
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDateTime transactionTime;
    private String transferCode;
    private String status;
}
