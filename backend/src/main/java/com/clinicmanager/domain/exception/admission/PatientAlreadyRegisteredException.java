package com.clinicmanager.domain.exception.admission;

import com.clinicmanager.domain.exception.BusinessException;

public class PatientAlreadyRegisteredException extends BusinessException {
    public PatientAlreadyRegisteredException(String message) {
        super(message);
    }
}
