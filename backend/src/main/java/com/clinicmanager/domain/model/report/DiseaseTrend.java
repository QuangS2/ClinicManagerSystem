package com.clinicmanager.domain.model.report;

import com.clinicmanager.domain.exception.report.InvalidReportDataException;

public class DiseaseTrend {
    private final String diseaseName;
    private final long caseCount;
    private final double percentage;

    public DiseaseTrend(String diseaseName, long caseCount, double percentage) {
        this.diseaseName = diseaseName;
        this.caseCount = caseCount;
        this.percentage = percentage;
        validate();
    }

    private void validate() {
        if (diseaseName == null || diseaseName.trim().isEmpty()) {
            throw new InvalidReportDataException("Tên bệnh không được để trống.");
        }
        if (caseCount < 0) {
            throw new InvalidReportDataException("Số ca mắc không được nhỏ hơn 0.");
        }
        if (percentage < 0.0 || Double.isNaN(percentage) || Double.isInfinite(percentage)) {
            throw new InvalidReportDataException("Tỷ lệ phần trăm không hợp lệ.");
        }
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public long getCaseCount() {
        return caseCount;
    }

    public double getPercentage() {
        return percentage;
    }
}
