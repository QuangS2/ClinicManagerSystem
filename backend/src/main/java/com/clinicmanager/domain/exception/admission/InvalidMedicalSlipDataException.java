package com.clinicmanager.domain.exception.admission;

import com.clinicmanager.domain.exception.BusinessException;

public class InvalidMedicalSlipDataException extends BusinessException {
    public InvalidMedicalSlipDataException(String message) {
        super(message);
    }
}
