package com.clinicmanager.domain.exception.appointment;

import com.clinicmanager.domain.exception.BusinessException;

public class AppointmentNotFoundException extends BusinessException {
    public AppointmentNotFoundException(String message) {
        super(message);
    }
}
