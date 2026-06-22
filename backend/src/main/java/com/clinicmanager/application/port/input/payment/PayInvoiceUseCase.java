package com.clinicmanager.application.port.input.payment;

import com.clinicmanager.application.dto.payment.PayInvoiceRequest;
import com.clinicmanager.application.dto.payment.PaymentTransactionDto;

public interface PayInvoiceUseCase {
    PaymentTransactionDto payInvoice(PayInvoiceRequest request);
}
