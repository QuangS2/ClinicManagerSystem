package com.clinicmanager.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaInvoiceRepository extends JpaRepository<InvoiceEntity, String> {
    Optional<InvoiceEntity> findByMedicalSlipId(String medicalSlipId);
}
