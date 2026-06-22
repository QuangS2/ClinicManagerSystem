package com.clinicmanager.application.port.input.payment;

import com.clinicmanager.application.dto.payment.InvoicePrintDto;
import java.util.UUID;

public interface PrintInvoiceUseCase {
    InvoicePrintDto getInvoicePrintDetails(UUID invoiceId);
}
