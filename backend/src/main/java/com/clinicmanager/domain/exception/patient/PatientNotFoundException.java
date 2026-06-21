package com.clinicmanager.domain.exception.patient;

import com.clinicmanager.domain.exception.BusinessException;

public class PatientNotFoundException extends BusinessException {
    public PatientNotFoundException(String message) {
        super(message);
    }
}
