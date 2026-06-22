package com.clinicmanager.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalExaminationRatioReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private long totalExaminations;
    private long totalExaminationsWithLabTests;
    private long totalExaminationsWithMedicines;
    private double overallLabTestRatio;
    private double overallMedicineRatio;
    private List<DailyExaminationRatioDto> dailyRatios;
}
