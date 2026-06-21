package com.clinicmanager.infrastructure.persistence.admission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Collection;

@Repository
public interface JpaMedicalSlipRepository extends JpaRepository<MedicalSlipEntity, String> {
    boolean existsByPatientIdAndExaminationDateAndStatusIn(String patientId, LocalDate examinationDate, Collection<String> statuses);
}
