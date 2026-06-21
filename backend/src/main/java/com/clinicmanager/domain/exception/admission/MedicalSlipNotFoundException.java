package com.clinicmanager.domain.exception.admission;

import com.clinicmanager.domain.exception.BusinessException;

public class MedicalSlipNotFoundException extends BusinessException {
    public MedicalSlipNotFoundException(String message) {
        super(message);
    }
}
