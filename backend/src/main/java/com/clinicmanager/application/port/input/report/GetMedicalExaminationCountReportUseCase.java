package com.clinicmanager.application.port.input.report;

import com.clinicmanager.application.dto.report.MedicalExaminationCountReportDto;
import java.time.LocalDate;

public interface GetMedicalExaminationCountReportUseCase {
    MedicalExaminationCountReportDto getReport(LocalDate startDate, LocalDate endDate);
}
