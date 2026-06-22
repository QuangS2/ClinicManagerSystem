package com.clinicmanager.application.port.output.payment;

import com.clinicmanager.domain.model.payment.Invoice;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepositoryPort {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(UUID id);
    Optional<Invoice> findByMedicalSlipId(UUID medicalSlipId);
}
