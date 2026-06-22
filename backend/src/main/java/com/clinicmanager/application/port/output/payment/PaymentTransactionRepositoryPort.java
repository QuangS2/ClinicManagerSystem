package com.clinicmanager.application.port.output.payment;

import com.clinicmanager.domain.model.payment.PaymentTransaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepositoryPort {
    PaymentTransaction save(PaymentTransaction transaction);
    Optional<PaymentTransaction> findById(UUID id);
    List<PaymentTransaction> findByInvoiceId(UUID invoiceId);
}
