package com.clinicmanager.domain.model.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyRevenue {
    private final LocalDate date;
    private final BigDecimal revenue;

    public DailyRevenue(LocalDate date, BigDecimal revenue) {
        this.date = date;
        this.revenue = revenue != null ? revenue : BigDecimal.ZERO;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}
