package com.clinicmanager.application.mapper.report;

import com.clinicmanager.application.dto.report.DailyExaminationCountDto;
import com.clinicmanager.application.dto.report.MedicalExaminationCountReportDto;
import com.clinicmanager.domain.model.report.DailyExaminationCount;
import com.clinicmanager.domain.model.report.MedicalExaminationCountReport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    DailyExaminationCountDto toDto(DailyExaminationCount domain);
    MedicalExaminationCountReportDto toDto(MedicalExaminationCountReport domain);
}
