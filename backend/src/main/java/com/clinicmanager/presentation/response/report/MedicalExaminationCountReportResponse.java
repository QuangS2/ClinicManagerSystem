package com.clinicmanager.presentation.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalExaminationCountReportResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyExaminationCountResponse> dailyCounts;
    private long totalExaminations;
    private Map<String, Long> statusBreakdown;
    private double averagePerDay;
}
