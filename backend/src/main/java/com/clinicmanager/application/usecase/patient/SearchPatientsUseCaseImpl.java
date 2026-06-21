package com.clinicmanager.application.usecase.patient;

import com.clinicmanager.application.dto.patient.PatientDto;
import com.clinicmanager.application.mapper.patient.PatientMapper;
import com.clinicmanager.application.port.input.patient.SearchPatientsUseCase;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.domain.model.patient.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchPatientsUseCaseImpl implements SearchPatientsUseCase {

    private final PatientRepositoryPort patientRepositoryPort;
    private final PatientMapper patientMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto> search(String name, String phone) {
        String searchName = name != null ? name.trim() : "";
        String searchPhone = phone != null ? phone.trim() : "";

        List<Patient> patients = patientRepositoryPort.search(searchName, searchPhone);

        return patients.stream()
                .limit(100)
                .map(patientMapper::toDto)
                .collect(Collectors.toList());
    }
}
