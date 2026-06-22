package com.clinicmanager.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaPaymentTransactionRepository extends JpaRepository<PaymentTransactionEntity, String> {
    List<PaymentTransactionEntity> findByInvoiceId(String invoiceId);
}
