package com.clinicmanager.infrastructure.persistence.examination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaLabTestRepository extends JpaRepository<LabTestEntity, String> {
    List<LabTestEntity> findByMedicalSlipId(String medicalSlipId);
}
