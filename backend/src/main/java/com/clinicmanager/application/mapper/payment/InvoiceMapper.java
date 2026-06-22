package com.clinicmanager.application.mapper.payment;

import com.clinicmanager.application.dto.payment.InvoiceDto;
import com.clinicmanager.domain.model.payment.Invoice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceDto toDto(Invoice invoice);
    Invoice toDomain(InvoiceDto dto);
}
