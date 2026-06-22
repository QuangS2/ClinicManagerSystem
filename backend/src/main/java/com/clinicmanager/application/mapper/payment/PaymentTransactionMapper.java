package com.clinicmanager.application.mapper.payment;

import com.clinicmanager.application.dto.payment.PaymentTransactionDto;
import com.clinicmanager.domain.model.payment.PaymentTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {
    PaymentTransactionDto toDto(PaymentTransaction domain);
    PaymentTransaction toDomain(PaymentTransactionDto dto);
}
