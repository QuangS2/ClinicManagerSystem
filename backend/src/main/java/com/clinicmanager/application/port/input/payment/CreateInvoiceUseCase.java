package com.clinicmanager.application.port.input.payment;

import com.clinicmanager.application.dto.payment.CreateInvoiceRequest;
import com.clinicmanager.application.dto.payment.InvoiceDto;

public interface CreateInvoiceUseCase {
    InvoiceDto createInvoice(CreateInvoiceRequest request);
}
