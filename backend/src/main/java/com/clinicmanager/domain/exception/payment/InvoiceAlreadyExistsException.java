package com.clinicmanager.domain.exception.payment;

import com.clinicmanager.domain.exception.BusinessException;

public class InvoiceAlreadyExistsException extends BusinessException {
    public InvoiceAlreadyExistsException(String message) {
        super(message);
    }
}
