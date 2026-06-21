package com.clinicmanager.domain.exception.examination;

import com.clinicmanager.domain.exception.BusinessException;

public class LabTestNotFoundException extends BusinessException {
    public LabTestNotFoundException(String message) {
        super(message);
    }
}
