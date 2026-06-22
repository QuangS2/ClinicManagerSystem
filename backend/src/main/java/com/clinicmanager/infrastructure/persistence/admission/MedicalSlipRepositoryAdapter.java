package com.clinicmanager.infrastructure.persistence.admission;

import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MedicalSlipRepositoryAdapter implements MedicalSlipRepositoryPort {

    private final JpaMedicalSlipRepository jpaMedicalSlipRepository;
    private final MedicalSlipPersistenceMapper persistenceMapper;

    @Override
    public MedicalSlip save(MedicalSlip medicalSlip) {
        MedicalSlipEntity entity = persistenceMapper.toEntity(medicalSlip);
        MedicalSlipEntity savedEntity = jpaMedicalSlipRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<MedicalSlip> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return jpaMedicalSlipRepository.findById(id.toString()).map(persistenceMapper::toDomain);
    }

    @Override
    public boolean existsActiveSlipByPatientIdAndDate(UUID patientId, LocalDate date) {
        List<String> activeStatuses = Arrays.asList(
                MedicalSlipStatus.WAITING.name(),
                MedicalSlipStatus.EXAMINING.name()
        );
        return jpaMedicalSlipRepository.existsByPatientIdAndExaminationDateAndStatusIn(
                patientId.toString(),
                date,
                activeStatuses
        );
    }

    @Override
    public List<MedicalSlip> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return jpaMedicalSlipRepository.findByExaminationDateBetween(startDate, endDate)
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }
}
