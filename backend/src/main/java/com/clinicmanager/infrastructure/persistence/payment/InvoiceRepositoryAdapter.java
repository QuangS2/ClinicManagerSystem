package com.clinicmanager.infrastructure.persistence.payment;

import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.domain.model.payment.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvoiceRepositoryAdapter implements InvoiceRepositoryPort {

    private final JpaInvoiceRepository jpaInvoiceRepository;
    private final InvoicePersistenceMapper persistenceMapper;

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceEntity entity = persistenceMapper.toEntity(invoice);
        InvoiceEntity savedEntity = jpaInvoiceRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Invoice> findById(UUID id) {
        return jpaInvoiceRepository.findById(id.toString())
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<Invoice> findByMedicalSlipId(UUID medicalSlipId) {
        return jpaInvoiceRepository.findByMedicalSlipId(medicalSlipId.toString())
                .map(persistenceMapper::toDomain);
    }

    @Override
    public java.util.List<Invoice> findByDateRangeAndStatus(java.time.LocalDate startDate, java.time.LocalDate endDate, com.clinicmanager.domain.model.payment.InvoiceStatus status) {
        java.time.LocalDateTime start = startDate.atStartOfDay();
        java.time.LocalDateTime end = endDate.atTime(23, 59, 59, 999999999);
        return jpaInvoiceRepository.findByCreatedAtBetweenAndStatus(start, end, status.name())
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }
}
