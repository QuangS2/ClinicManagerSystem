package com.clinicmanager.application.mapper.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.domain.model.examination.LabTest;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LabTestMapper {
    LabTestDto toDto(LabTest labTest);
    List<LabTestDto> toDtoList(List<LabTest> labTests);
}
