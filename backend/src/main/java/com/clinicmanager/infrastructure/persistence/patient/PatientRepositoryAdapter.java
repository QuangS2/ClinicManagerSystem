package com.clinicmanager.infrastructure.persistence.patient;

import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.domain.model.patient.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepositoryPort {

    private final JpaPatientRepository jpaPatientRepository;
    private final PatientPersistenceMapper persistenceMapper;

    @Override
    public Patient save(Patient patient) {
        PatientEntity entity = persistenceMapper.toEntity(patient);
        PatientEntity savedEntity = jpaPatientRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return jpaPatientRepository.findById(id.toString()).map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<Patient> findByPhone(String phone) {
        return jpaPatientRepository.findByPhone(phone).map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return jpaPatientRepository.findByEmail(email).map(persistenceMapper::toDomain);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return jpaPatientRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaPatientRepository.existsByEmail(email);
    }

    @Override
    public List<Patient> search(String name, String phone) {
        List<PatientEntity> entities = jpaPatientRepository
                .findByFullNameContainingIgnoreCaseAndPhoneContaining(name, phone);
        return entities.stream()
                .map(persistenceMapper::toDomain)
                .collect(Collectors.toList());
    }
}
