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
public class DiseaseTrendReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private long totalCases;
    private List<DiseaseTrendDto> trends;
}
