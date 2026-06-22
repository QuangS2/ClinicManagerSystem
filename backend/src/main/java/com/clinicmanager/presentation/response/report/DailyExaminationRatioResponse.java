package com.clinicmanager.presentation.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyExaminationRatioResponse {
    private LocalDate date;
    private long totalExaminations;
    private long examinationsWithLabTests;
    private long examinationsWithMedicines;
    private double labTestRatio;
    private double medicineRatio;
}
