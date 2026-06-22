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
}
