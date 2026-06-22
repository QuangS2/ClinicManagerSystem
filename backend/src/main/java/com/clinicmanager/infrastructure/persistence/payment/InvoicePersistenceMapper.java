package com.clinicmanager.infrastructure.persistence.payment;

import com.clinicmanager.domain.model.payment.Invoice;
import com.clinicmanager.domain.model.payment.InvoiceStatus;
import org.mapstruct.Mapper;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface InvoicePersistenceMapper {

    InvoiceEntity toEntity(Invoice domain);

    Invoice toDomain(InvoiceEntity entity);

    default String map(UUID value) {
        return value != null ? value.toString() : null;
    }

    default UUID map(String value) {
        return value != null ? UUID.fromString(value) : null;
    }

    default String mapStatus(InvoiceStatus status) {
        return status != null ? status.name() : null;
    }

    default InvoiceStatus mapStatus(String status) {
        return status != null ? InvoiceStatus.valueOf(status) : null;
    }
}
