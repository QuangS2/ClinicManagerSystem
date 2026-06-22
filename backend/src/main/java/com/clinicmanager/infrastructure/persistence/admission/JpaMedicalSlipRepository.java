package com.clinicmanager.infrastructure.persistence.admission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface JpaMedicalSlipRepository extends JpaRepository<MedicalSlipEntity, String> {
    boolean existsByPatientIdAndExaminationDateAndStatusIn(String patientId, LocalDate examinationDate, Collection<String> statuses);
    List<MedicalSlipEntity> findByExaminationDateBetween(LocalDate startDate, LocalDate endDate);
}
