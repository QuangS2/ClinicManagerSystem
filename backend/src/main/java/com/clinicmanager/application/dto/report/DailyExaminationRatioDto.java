package com.clinicmanager.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyExaminationRatioDto {
    private LocalDate date;
    private long totalExaminations;
    private long examinationsWithLabTests;
    private long examinationsWithMedicines;
    private double labTestRatio;
    private double medicineRatio;
}
