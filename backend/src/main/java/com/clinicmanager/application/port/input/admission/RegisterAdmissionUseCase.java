package com.clinicmanager.application.port.input.admission;

import com.clinicmanager.application.dto.admission.MedicalSlipDto;
import com.clinicmanager.application.dto.admission.RegisterAdmissionRequest;

public interface RegisterAdmissionUseCase {
    MedicalSlipDto register(RegisterAdmissionRequest request);
}
