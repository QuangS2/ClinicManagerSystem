package com.clinicmanager.application.port.output.payment;

import com.clinicmanager.domain.model.payment.Invoice;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepositoryPort {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(UUID id);
    Optional<Invoice> findByMedicalSlipId(UUID medicalSlipId);
    java.util.List<Invoice> findByDateRangeAndStatus(java.time.LocalDate startDate, java.time.LocalDate endDate, com.clinicmanager.domain.model.payment.InvoiceStatus status);
}
