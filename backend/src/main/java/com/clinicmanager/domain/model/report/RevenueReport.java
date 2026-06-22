package com.clinicmanager.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RevenueReport {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal totalRevenue;
    private final BigDecimal clinicalExamRevenue;
    private final BigDecimal labTestRevenue;
    private final BigDecimal medicineRevenue;
    private final List<DailyRevenue> dailyRevenues;
    private final BigDecimal averageRevenuePerDay;

    public RevenueReport(LocalDate startDate, LocalDate endDate, BigDecimal totalRevenue,
                         BigDecimal clinicalExamRevenue, BigDecimal labTestRevenue, BigDecimal medicineRevenue,
                         List<DailyRevenue> dailyRevenues, BigDecimal averageRevenuePerDay) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.clinicalExamRevenue = clinicalExamRevenue != null ? clinicalExamRevenue : BigDecimal.ZERO;
        this.labTestRevenue = labTestRevenue != null ? labTestRevenue : BigDecimal.ZERO;
        this.medicineRevenue = medicineRevenue != null ? medicineRevenue : BigDecimal.ZERO;
        this.dailyRevenues = dailyRevenues;
        this.averageRevenuePerDay = averageRevenuePerDay != null ? averageRevenuePerDay : BigDecimal.ZERO;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public BigDecimal getClinicalExamRevenue() {
        return clinicalExamRevenue;
    }

    public BigDecimal getLabTestRevenue() {
        return labTestRevenue;
    }

    public BigDecimal getMedicineRevenue() {
        return medicineRevenue;
    }

    public List<DailyRevenue> getDailyRevenues() {
        return dailyRevenues;
    }

    public BigDecimal getAverageRevenuePerDay() {
        return averageRevenuePerDay;
    }
}
