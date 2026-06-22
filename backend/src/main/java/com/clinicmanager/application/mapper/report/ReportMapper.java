package com.clinicmanager.application.mapper.report;

import com.clinicmanager.application.dto.report.DailyExaminationCountDto;
import com.clinicmanager.application.dto.report.MedicalExaminationCountReportDto;
import com.clinicmanager.application.dto.report.DailyExaminationRatioDto;
import com.clinicmanager.application.dto.report.MedicalExaminationRatioReportDto;
import com.clinicmanager.application.dto.report.DailyRevenueDto;
import com.clinicmanager.application.dto.report.RevenueReportDto;
import com.clinicmanager.application.dto.report.DiseaseTrendDto;
import com.clinicmanager.application.dto.report.DiseaseTrendReportDto;
import com.clinicmanager.domain.model.report.DailyExaminationCount;
import com.clinicmanager.domain.model.report.MedicalExaminationCountReport;
import com.clinicmanager.domain.model.report.DailyExaminationRatio;
import com.clinicmanager.domain.model.report.MedicalExaminationRatioReport;
import com.clinicmanager.domain.model.report.DailyRevenue;
import com.clinicmanager.domain.model.report.RevenueReport;
import com.clinicmanager.domain.model.report.DiseaseTrend;
import com.clinicmanager.domain.model.report.DiseaseTrendReport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    DailyExaminationCountDto toDto(DailyExaminationCount domain);
    MedicalExaminationCountReportDto toDto(MedicalExaminationCountReport domain);
    DailyRevenueDto toDto(DailyRevenue domain);
    RevenueReportDto toDto(RevenueReport domain);
    DailyExaminationRatioDto toDto(DailyExaminationRatio domain);
    MedicalExaminationRatioReportDto toDto(MedicalExaminationRatioReport domain);
    DiseaseTrendDto toDto(DiseaseTrend domain);
    DiseaseTrendReportDto toDto(DiseaseTrendReport domain);
}
