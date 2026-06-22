package com.clinicmanager.domain.exception.payment;

import com.clinicmanager.domain.exception.BusinessException;

public class InvoiceNotFoundException extends BusinessException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
