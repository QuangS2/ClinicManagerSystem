package com.clinicmanager.application.mapper.admission;

import com.clinicmanager.application.dto.admission.MedicalSlipDto;
import com.clinicmanager.application.dto.admission.RegisterAdmissionRequest;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicalSlipMapper {
    MedicalSlipDto toDto(MedicalSlip medicalSlip);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "examinationDate", ignore = true)
    @Mapping(target = "symptoms", ignore = true)
    @Mapping(target = "pulse", ignore = true)
    @Mapping(target = "temperature", ignore = true)
    @Mapping(target = "bloodPressure", ignore = true)
    @Mapping(target = "weight", ignore = true)
    @Mapping(target = "height", ignore = true)
    MedicalSlip toDomain(RegisterAdmissionRequest request);
}
