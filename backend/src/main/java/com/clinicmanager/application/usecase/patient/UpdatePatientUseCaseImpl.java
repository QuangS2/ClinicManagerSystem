package com.clinicmanager.application.usecase.patient;

import com.clinicmanager.application.dto.patient.PatientDto;
import com.clinicmanager.application.dto.patient.UpdatePatientRequest;
import com.clinicmanager.application.mapper.patient.PatientMapper;
import com.clinicmanager.application.port.input.patient.UpdatePatientUseCase;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.domain.exception.patient.PatientAlreadyExistsException;
import com.clinicmanager.domain.exception.patient.PatientNotFoundException;
import com.clinicmanager.domain.model.patient.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdatePatientUseCaseImpl implements UpdatePatientUseCase {

    private final PatientRepositoryPort patientRepositoryPort;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientDto update(UUID id, UpdatePatientRequest request) {
        // 1. Check existence
        patientRepositoryPort.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Không tìm thấy bệnh nhân với ID: " + id));

        // 2. Check phone conflict with other patients
        Optional<Patient> patientWithPhone = patientRepositoryPort.findByPhone(request.getPhone());
        if (patientWithPhone.isPresent() && !patientWithPhone.get().getId().equals(id)) {
            throw new PatientAlreadyExistsException("Số điện thoại đã tồn tại ở một bệnh nhân khác.");
        }

        // 3. Check email conflict with other patients
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            Optional<Patient> patientWithEmail = patientRepositoryPort.findByEmail(request.getEmail().trim());
            if (patientWithEmail.isPresent() && !patientWithEmail.get().getId().equals(id)) {
                throw new PatientAlreadyExistsException("Email đã tồn tại ở một bệnh nhân khác.");
            }
        }

        // 4. Construct updated Domain Patient (runs validation)
        Patient updatedPatient = new Patient(
                id,
                request.getFullName(),
                request.getDob(),
                request.getGender(),
                request.getPhone(),
                request.getAddress(),
                request.getEmail()
        );

        // 5. Save
        Patient savedPatient = patientRepositoryPort.save(updatedPatient);

        // 6. Map to DTO and return
        return patientMapper.toDto(savedPatient);
    }
}
