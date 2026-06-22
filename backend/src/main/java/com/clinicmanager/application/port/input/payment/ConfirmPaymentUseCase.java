package com.clinicmanager.application.port.input.payment;

import com.clinicmanager.application.dto.payment.InvoiceDto;
import java.util.UUID;

public interface ConfirmPaymentUseCase {
    InvoiceDto confirmPayment(UUID transactionId);
}
