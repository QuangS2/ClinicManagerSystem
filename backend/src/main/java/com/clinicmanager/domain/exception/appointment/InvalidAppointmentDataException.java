package com.clinicmanager.domain.exception.appointment;

import com.clinicmanager.domain.exception.BusinessException;

public class InvalidAppointmentDataException extends BusinessException {
    public InvalidAppointmentDataException(String message) {
        super(message);
    }
}
