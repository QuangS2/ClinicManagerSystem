package com.clinicmanager.application.port.input.patient;

import com.clinicmanager.application.dto.patient.PatientDto;
import java.util.List;

public interface SearchPatientsUseCase {
    List<PatientDto> search(String name, String phone);
}
