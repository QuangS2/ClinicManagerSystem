package com.clinicmanager.domain.model.report;

import java.time.LocalDate;

public class DailyExaminationCount {
    private final LocalDate date;
    private final long count;

    public DailyExaminationCount(LocalDate date, long count) {
        this.date = date;
        this.count = count;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getCount() {
        return count;
    }
}
