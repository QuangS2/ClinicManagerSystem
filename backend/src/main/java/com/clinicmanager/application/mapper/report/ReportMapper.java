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
    com.clinicmanager.application.dto.report.DailyRevenueDto toDto(com.clinicmanager.domain.model.report.DailyRevenue domain);
    com.clinicmanager.application.dto.report.RevenueReportDto toDto(com.clinicmanager.domain.model.report.RevenueReport domain);
}
