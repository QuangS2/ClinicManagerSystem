package com.clinicmanager.application.port.input.report;

import com.clinicmanager.application.dto.report.RevenueReportDto;
import java.time.LocalDate;

public interface GetRevenueReportUseCase {
    RevenueReportDto getReport(LocalDate startDate, LocalDate endDate);
}
