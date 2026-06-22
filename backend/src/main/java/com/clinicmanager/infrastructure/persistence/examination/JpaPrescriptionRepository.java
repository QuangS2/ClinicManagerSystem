package com.clinicmanager.infrastructure.persistence.examination;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaPrescriptionRepository extends JpaRepository<PrescriptionEntity, String> {
    Optional<PrescriptionEntity> findByMedicalSlipId(String medicalSlipId);
    boolean existsByMedicalSlipId(String medicalSlipId);
    java.util.List<PrescriptionEntity> findByMedicalSlipIdIn(java.util.List<String> medicalSlipIds);
}
