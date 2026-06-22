package com.clinicmanager.application.port.input.report;

import com.clinicmanager.application.dto.report.DiseaseTrendReportDto;
import java.time.LocalDate;

public interface GetDiseaseTrendReportUseCase {
    DiseaseTrendReportDto getReport(LocalDate startDate, LocalDate endDate);
}
