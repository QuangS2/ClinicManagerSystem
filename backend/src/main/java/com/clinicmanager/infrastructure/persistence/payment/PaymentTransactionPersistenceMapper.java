package com.clinicmanager.infrastructure.persistence.payment;

import com.clinicmanager.domain.model.payment.PaymentTransaction;
import com.clinicmanager.domain.model.payment.PaymentMethod;
import com.clinicmanager.domain.model.payment.TransactionStatus;
import org.mapstruct.Mapper;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaymentTransactionPersistenceMapper {

    PaymentTransactionEntity toEntity(PaymentTransaction domain);

    PaymentTransaction toDomain(PaymentTransactionEntity entity);

    default String map(UUID value) {
        return value != null ? value.toString() : null;
    }

    default UUID map(String value) {
        return value != null ? UUID.fromString(value) : null;
    }

    default String mapMethod(PaymentMethod method) {
        return method != null ? method.name() : null;
    }

    default PaymentMethod mapMethod(String method) {
        return method != null ? PaymentMethod.valueOf(method) : null;
    }

    default String mapStatus(TransactionStatus status) {
        return status != null ? status.name() : null;
    }

    default TransactionStatus mapStatus(String status) {
        return status != null ? TransactionStatus.valueOf(status) : null;
    }
}
