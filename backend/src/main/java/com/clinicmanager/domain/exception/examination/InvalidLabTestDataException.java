package com.clinicmanager.domain.exception.examination;

import com.clinicmanager.domain.exception.BusinessException;

public class InvalidLabTestDataException extends BusinessException {
    public InvalidLabTestDataException(String message) {
        super(message);
    }
}
