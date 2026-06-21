package com.clinicmanager.application.port.output.admission;

import com.clinicmanager.domain.model.admission.MedicalSlip;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MedicalSlipRepositoryPort {
    MedicalSlip save(MedicalSlip medicalSlip);
    Optional<MedicalSlip> findById(UUID id);
    boolean existsActiveSlipByPatientIdAndDate(UUID patientId, LocalDate date);
}
