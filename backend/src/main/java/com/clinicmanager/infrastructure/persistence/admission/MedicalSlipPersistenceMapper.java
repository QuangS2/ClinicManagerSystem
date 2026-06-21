package com.clinicmanager.infrastructure.persistence.admission;

import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MedicalSlipPersistenceMapper {

    @Mapping(target = "id", expression = "java(domain.getId() != null ? domain.getId().toString() : null)")
    @Mapping(target = "patientId", expression = "java(domain.getPatientId() != null ? domain.getPatientId().toString() : null)")
    @Mapping(target = "status", expression = "java(domain.getStatus() != null ? domain.getStatus().name() : null)")
    MedicalSlipEntity toEntity(MedicalSlip domain);

    default MedicalSlip toDomain(MedicalSlipEntity entity) {
        if (entity == null) {
            return null;
        }
        UUID id = entity.getId() != null ? UUID.fromString(entity.getId()) : null;
        UUID patientId = entity.getPatientId() != null ? UUID.fromString(entity.getPatientId()) : null;
        MedicalSlipStatus status = entity.getStatus() != null ? MedicalSlipStatus.valueOf(entity.getStatus()) : null;

        return new MedicalSlip(
            id,
            entity.getExaminationDate(),
            status,
            patientId,
            entity.getSymptoms(),
            entity.getPulse(),
            entity.getTemperature(),
            entity.getBloodPressure(),
            entity.getWeight(),
            entity.getHeight()
        );
    }
}
