package com.clinicmanager.application.dto.payment;

import com.clinicmanager.domain.model.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayInvoiceRequest {
    private UUID invoiceId;
    private PaymentMethod paymentMethod;
    private String transferCode;
}
