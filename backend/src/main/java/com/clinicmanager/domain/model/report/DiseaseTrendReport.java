package com.clinicmanager.domain.model.report;

import com.clinicmanager.domain.exception.report.InvalidReportDataException;
import com.clinicmanager.domain.exception.report.InvalidReportDateRangeException;
import java.time.LocalDate;
import java.util.List;

public class DiseaseTrendReport {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final long totalCases;
    private final List<DiseaseTrend> trends;

    public DiseaseTrendReport(LocalDate startDate, LocalDate endDate, long totalCases, List<DiseaseTrend> trends) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCases = totalCases;
        this.trends = trends != null ? List.copyOf(trends) : List.of();
        validate();
    }

    private void validate() {
        if (startDate == null || endDate == null) {
            throw new InvalidReportDataException("Ngày bắt đầu và ngày kết thúc không được để trống.");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidReportDateRangeException("Ngày bắt đầu không được nằm sau ngày kết thúc.");
        }
        if (totalCases < 0) {
            throw new InvalidReportDataException("Tổng số ca mắc không được nhỏ hơn 0.");
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public long getTotalCases() {
        return totalCases;
    }

    public List<DiseaseTrend> getTrends() {
        return trends;
    }
}
