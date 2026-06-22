package com.clinicmanager.domain.exception.report;

import com.clinicmanager.domain.exception.BusinessException;

public class InvalidReportDataException extends BusinessException {
    public InvalidReportDataException(String message) {
        super(message);
    }
}
