package com.clinicmanager.application.port.output.patient;

import com.clinicmanager.domain.model.patient.Patient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepositoryPort {
    Patient save(Patient patient);
    Optional<Patient> findById(UUID id);
    Optional<Patient> findByPhone(String phone);
    Optional<Patient> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    List<Patient> search(String name, String phone);
}
