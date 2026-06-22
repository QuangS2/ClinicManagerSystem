package com.clinicmanager.domain.model.report;

import java.time.LocalDate;
import java.util.List;

public class MedicalExaminationRatioReport {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final long totalExaminations;
    private final long totalExaminationsWithLabTests;
    private final long totalExaminationsWithMedicines;
    private final double overallLabTestRatio;
    private final double overallMedicineRatio;
    private final List<DailyExaminationRatio> dailyRatios;

    public MedicalExaminationRatioReport(LocalDate startDate, LocalDate endDate, long totalExaminations,
                                         long totalExaminationsWithLabTests, long totalExaminationsWithMedicines,
                                         double overallLabTestRatio, double overallMedicineRatio,
                                         List<DailyExaminationRatio> dailyRatios) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalExaminations = totalExaminations;
        this.totalExaminationsWithLabTests = totalExaminationsWithLabTests;
        this.totalExaminationsWithMedicines = totalExaminationsWithMedicines;
        this.overallLabTestRatio = overallLabTestRatio;
        this.overallMedicineRatio = overallMedicineRatio;
        this.dailyRatios = dailyRatios;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public long getTotalExaminations() {
        return totalExaminations;
    }

    public long getTotalExaminationsWithLabTests() {
        return totalExaminationsWithLabTests;
    }

    public long getTotalExaminationsWithMedicines() {
        return totalExaminationsWithMedicines;
    }

    public double getOverallLabTestRatio() {
        return overallLabTestRatio;
    }

    public double getOverallMedicineRatio() {
        return overallMedicineRatio;
    }

    public List<DailyExaminationRatio> getDailyRatios() {
        return dailyRatios;
    }
}
