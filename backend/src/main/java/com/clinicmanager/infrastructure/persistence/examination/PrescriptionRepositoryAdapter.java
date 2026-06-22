package com.clinicmanager.infrastructure.persistence.examination;

import com.clinicmanager.application.port.output.examination.PrescriptionRepositoryPort;
import com.clinicmanager.domain.model.examination.Prescription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PrescriptionRepositoryAdapter implements PrescriptionRepositoryPort {

    private final JpaPrescriptionRepository jpaPrescriptionRepository;
    private final PrescriptionPersistenceMapper persistenceMapper;

    @Override
    public Prescription save(Prescription prescription) {
        PrescriptionEntity entity = persistenceMapper.toEntityWithItems(prescription);
        PrescriptionEntity savedEntity = jpaPrescriptionRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Prescription> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return jpaPrescriptionRepository.findById(id.toString()).map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<Prescription> findByMedicalSlipId(UUID medicalSlipId) {
        if (medicalSlipId == null) {
            return Optional.empty();
        }
        return jpaPrescriptionRepository.findByMedicalSlipId(medicalSlipId.toString()).map(persistenceMapper::toDomain);
    }

    @Override
    public boolean existsByMedicalSlipId(UUID medicalSlipId) {
        if (medicalSlipId == null) {
            return false;
        }
        return jpaPrescriptionRepository.existsByMedicalSlipId(medicalSlipId.toString());
    }

    @Override
    public java.util.List<Prescription> findByMedicalSlipIds(java.util.List<UUID> medicalSlipIds) {
        if (medicalSlipIds == null || medicalSlipIds.isEmpty()) {
            return java.util.List.of();
        }
        java.util.List<String> idsStr = medicalSlipIds.stream().map(UUID::toString).toList();
        return jpaPrescriptionRepository.findByMedicalSlipIdIn(idsStr).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }
}
