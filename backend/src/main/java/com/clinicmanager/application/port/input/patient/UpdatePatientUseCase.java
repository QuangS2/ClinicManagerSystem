package com.clinicmanager.application.port.input.patient;

import com.clinicmanager.application.dto.patient.PatientDto;
import com.clinicmanager.application.dto.patient.UpdatePatientRequest;
import java.util.UUID;

public interface UpdatePatientUseCase {
    PatientDto update(UUID id, UpdatePatientRequest request);
}
