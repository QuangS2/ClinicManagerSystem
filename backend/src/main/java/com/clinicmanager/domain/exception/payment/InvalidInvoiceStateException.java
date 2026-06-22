package com.clinicmanager.domain.exception.payment;

import com.clinicmanager.domain.exception.BusinessException;

public class InvalidInvoiceStateException extends BusinessException {
    public InvalidInvoiceStateException(String message) {
        super(message);
    }
}
