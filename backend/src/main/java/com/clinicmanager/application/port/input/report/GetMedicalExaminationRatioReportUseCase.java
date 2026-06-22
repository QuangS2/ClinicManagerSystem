package com.clinicmanager.application.port.input.report;

import com.clinicmanager.application.dto.report.MedicalExaminationRatioReportDto;
import java.time.LocalDate;

public interface GetMedicalExaminationRatioReportUseCase {
    MedicalExaminationRatioReportDto getReport(LocalDate startDate, LocalDate endDate);
}
