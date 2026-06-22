package com.clinicmanager.infrastructure.persistence.examination;

import com.clinicmanager.application.port.output.examination.MedicalRecordRepositoryPort;
import com.clinicmanager.domain.model.examination.MedicalRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MedicalRecordRepositoryAdapter implements MedicalRecordRepositoryPort {

    private final JpaMedicalRecordRepository jpaMedicalRecordRepository;
    private final MedicalRecordPersistenceMapper persistenceMapper;

    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        MedicalRecordEntity entity = persistenceMapper.toEntity(medicalRecord);
        MedicalRecordEntity savedEntity = jpaMedicalRecordRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByMedicalSlipId(UUID medicalSlipId) {
        return jpaMedicalRecordRepository.existsByMedicalSlipId(medicalSlipId.toString());
    }

    @Override
    public Optional<MedicalRecord> findById(UUID id) {
        return jpaMedicalRecordRepository.findById(id.toString())
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<MedicalRecord> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return jpaMedicalRecordRepository.findByCreatedDateBetween(startDate, endDate).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }
}
