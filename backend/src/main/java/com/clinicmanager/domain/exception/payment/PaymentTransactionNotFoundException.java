package com.clinicmanager.domain.exception.payment;

import com.clinicmanager.domain.exception.BusinessException;

public class PaymentTransactionNotFoundException extends BusinessException {
    public PaymentTransactionNotFoundException(String message) {
        super(message);
    }
}
