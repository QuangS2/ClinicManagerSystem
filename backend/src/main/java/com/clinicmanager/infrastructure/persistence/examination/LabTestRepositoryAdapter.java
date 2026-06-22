package com.clinicmanager.infrastructure.persistence.examination;

import com.clinicmanager.application.port.output.examination.LabTestRepositoryPort;
import com.clinicmanager.domain.model.examination.LabTest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LabTestRepositoryAdapter implements LabTestRepositoryPort {

    private final JpaLabTestRepository jpaLabTestRepository;
    private final LabTestPersistenceMapper persistenceMapper;

    @Override
    public LabTest save(LabTest labTest) {
        LabTestEntity entity = persistenceMapper.toEntity(labTest);
        LabTestEntity savedEntity = jpaLabTestRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public List<LabTest> saveAll(List<LabTest> labTests) {
        List<LabTestEntity> entities = persistenceMapper.toEntityList(labTests);
        List<LabTestEntity> savedEntities = jpaLabTestRepository.saveAll(entities);
        return persistenceMapper.toDomainList(savedEntities);
    }

    @Override
    public Optional<LabTest> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return jpaLabTestRepository.findById(id.toString()).map(persistenceMapper::toDomain);
    }

    @Override
    public List<LabTest> findByMedicalSlipId(UUID medicalSlipId) {
        if (medicalSlipId == null) {
            return List.of();
        }
        return jpaLabTestRepository.findByMedicalSlipId(medicalSlipId.toString()).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<LabTest> findByMedicalSlipIds(List<UUID> medicalSlipIds) {
        if (medicalSlipIds == null || medicalSlipIds.isEmpty()) {
            return List.of();
        }
        List<String> idsStr = medicalSlipIds.stream().map(UUID::toString).toList();
        return jpaLabTestRepository.findByMedicalSlipIdIn(idsStr).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }
}
