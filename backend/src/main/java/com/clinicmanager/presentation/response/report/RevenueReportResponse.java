package com.clinicmanager.presentation.response.report;

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
public class RevenueReportResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private BigDecimal clinicalExamRevenue;
    private BigDecimal labTestRevenue;
    private BigDecimal medicineRevenue;
    private List<DailyRevenueResponse> dailyRevenues;
    private BigDecimal averageRevenuePerDay;
}
