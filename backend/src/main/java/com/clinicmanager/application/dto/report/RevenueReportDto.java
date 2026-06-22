package com.clinicmanager.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal clinicalExamRevenue;
    private BigDecimal labTestRevenue;
    private BigDecimal medicineRevenue;
    private List<DailyRevenueDto> dailyRevenues;
    private BigDecimal averageRevenuePerDay;
}
