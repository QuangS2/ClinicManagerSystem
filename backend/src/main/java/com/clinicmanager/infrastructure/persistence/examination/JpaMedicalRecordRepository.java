package com.clinicmanager.infrastructure.persistence.examination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaMedicalRecordRepository extends JpaRepository<MedicalRecordEntity, String> {
    boolean existsByMedicalSlipId(String medicalSlipId);
    List<MedicalRecordEntity> findByCreatedDateBetween(LocalDate startDate, LocalDate endDate);
}
