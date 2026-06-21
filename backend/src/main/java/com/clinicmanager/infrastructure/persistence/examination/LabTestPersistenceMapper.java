package com.clinicmanager.infrastructure.persistence.examination;

import com.clinicmanager.domain.model.examination.LabTest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LabTestPersistenceMapper {

    @Mapping(target = "id", expression = "java(domain.getId() != null ? domain.getId().toString() : null)")
    @Mapping(target = "medicalSlipId", expression = "java(domain.getMedicalSlipId() != null ? domain.getMedicalSlipId().toString() : null)")
    LabTestEntity toEntity(LabTest domain);

    List<LabTestEntity> toEntityList(List<LabTest> domains);

    default LabTest toDomain(LabTestEntity entity) {
        if (entity == null) {
            return null;
        }
        UUID id = entity.getId() != null ? UUID.fromString(entity.getId()) : null;
        UUID medicalSlipId = entity.getMedicalSlipId() != null ? UUID.fromString(entity.getMedicalSlipId()) : null;

        return new LabTest(
            id,
            entity.getTestType(),
            entity.getResult(),
            entity.getTestDate(),
            medicalSlipId
        );
    }

    List<LabTest> toDomainList(List<LabTestEntity> entities);
}
