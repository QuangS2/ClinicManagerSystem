package com.clinicmanager.presentation.response.payment;

import com.clinicmanager.application.dto.examination.ExaminationFeeBreakdownDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePrintResponse {
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID medicalSlipId;
    private String patientName;
    private String patientPhone;
    private String patientAddress;
    private LocalDate examinationDate;
    private ExaminationFeeBreakdownDto.ServiceItem examinationService;
    private List<ExaminationFeeBreakdownDto.ServiceItem> labTests;
    private List<ExaminationFeeBreakdownDto.MedicineItem> medicines;
    private BigDecimal totalAmount;
    private String totalAmountInWords;
    private String status;
    private String paymentMethod;
    private LocalDateTime paymentTime;
}
