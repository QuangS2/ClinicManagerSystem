package com.clinicmanager.application.port.input.examination;

import com.clinicmanager.application.dto.admission.MedicalSlipDto;
import com.clinicmanager.application.dto.examination.PerformClinicalExamRequest;
import java.util.UUID;

public interface PerformClinicalExamUseCase {
    MedicalSlipDto perform(UUID medicalSlipId, PerformClinicalExamRequest request);
}
