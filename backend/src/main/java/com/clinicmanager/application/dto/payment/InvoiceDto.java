package com.clinicmanager.application.dto.payment;

import com.clinicmanager.domain.model.payment.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private UUID id;
    private String invoiceNumber;
    private UUID medicalSlipId;
    private UUID patientId;
    private BigDecimal clinicalExamAmount;
    private BigDecimal labTestAmount;
    private BigDecimal medicineAmount;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private LocalDateTime createdAt;
}
