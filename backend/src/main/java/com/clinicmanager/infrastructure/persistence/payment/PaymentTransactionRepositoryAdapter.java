package com.clinicmanager.infrastructure.persistence.payment;

import com.clinicmanager.application.port.output.payment.PaymentTransactionRepositoryPort;
import com.clinicmanager.domain.model.payment.PaymentTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentTransactionRepositoryAdapter implements PaymentTransactionRepositoryPort {

    private final JpaPaymentTransactionRepository jpaPaymentTransactionRepository;
    private final PaymentTransactionPersistenceMapper persistenceMapper;

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        PaymentTransactionEntity entity = persistenceMapper.toEntity(transaction);
        PaymentTransactionEntity savedEntity = jpaPaymentTransactionRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PaymentTransaction> findById(UUID id) {
        return jpaPaymentTransactionRepository.findById(id.toString())
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<PaymentTransaction> findByInvoiceId(UUID invoiceId) {
        return jpaPaymentTransactionRepository.findByInvoiceId(invoiceId.toString()).stream()
                .map(persistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
