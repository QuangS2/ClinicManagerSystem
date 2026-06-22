package com.clinicmanager.domain.model.report;

import java.time.LocalDate;

public class DailyExaminationRatio {
    private final LocalDate date;
    private final long totalExaminations;
    private final long examinationsWithLabTests;
    private final long examinationsWithMedicines;
    private final double labTestRatio;
    private final double medicineRatio;

    public DailyExaminationRatio(LocalDate date, long totalExaminations,
                                 long examinationsWithLabTests, long examinationsWithMedicines,
                                 double labTestRatio, double medicineRatio) {
        this.date = date;
        this.totalExaminations = totalExaminations;
        this.examinationsWithLabTests = examinationsWithLabTests;
        this.examinationsWithMedicines = examinationsWithMedicines;
        this.labTestRatio = labTestRatio;
        this.medicineRatio = medicineRatio;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getTotalExaminations() {
        return totalExaminations;
    }

    public long getExaminationsWithLabTests() {
        return examinationsWithLabTests;
    }

    public long getExaminationsWithMedicines() {
        return examinationsWithMedicines;
    }

    public double getLabTestRatio() {
        return labTestRatio;
    }

    public double getMedicineRatio() {
        return medicineRatio;
    }
}
