package com.clinicmanager.application.port.output.examination;

import com.clinicmanager.domain.model.examination.LabTest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LabTestRepositoryPort {
    LabTest save(LabTest labTest);
    List<LabTest> saveAll(List<LabTest> labTests);
    Optional<LabTest> findById(UUID id);
    List<LabTest> findByMedicalSlipId(UUID medicalSlipId);
}
