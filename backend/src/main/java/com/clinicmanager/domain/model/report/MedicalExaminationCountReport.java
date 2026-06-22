package com.clinicmanager.domain.model.report;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MedicalExaminationCountReport {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<DailyExaminationCount> dailyCounts;
    private final long totalExaminations;
    private final Map<String, Long> statusBreakdown;
    private final double averagePerDay;

    public MedicalExaminationCountReport(LocalDate startDate, LocalDate endDate,
                                         List<DailyExaminationCount> dailyCounts,
                                         long totalExaminations,
                                         Map<String, Long> statusBreakdown,
                                         double averagePerDay) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyCounts = dailyCounts;
        this.totalExaminations = totalExaminations;
        this.statusBreakdown = statusBreakdown;
        this.averagePerDay = averagePerDay;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<DailyExaminationCount> getDailyCounts() {
        return dailyCounts;
    }

    public long getTotalExaminations() {
        return totalExaminations;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public double getAveragePerDay() {
        return averagePerDay;
    }
}
