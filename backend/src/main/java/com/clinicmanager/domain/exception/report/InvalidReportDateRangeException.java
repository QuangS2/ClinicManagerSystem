package com.clinicmanager.domain.exception.report;

import com.clinicmanager.domain.exception.BusinessException;

public class InvalidReportDateRangeException extends BusinessException {
    public InvalidReportDateRangeException(String message) {
        super(message);
    }
}
