package com.clinicmanager.presentation.response.report;

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
public class DiseaseTrendReportResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private long totalCases;
    private List<DiseaseTrendResponse> trends;
}
