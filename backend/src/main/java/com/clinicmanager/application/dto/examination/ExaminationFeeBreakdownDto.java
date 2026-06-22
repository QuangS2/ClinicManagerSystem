package com.clinicmanager.application.dto.examination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationFeeBreakdownDto {
    private UUID medicalSlipId;
    private UUID patientId;
    private String patientName;
    private ServiceItem examinationService;
    private List<ServiceItem> labTests;
    private List<MedicineItem> medicines;
    private BigDecimal totalAmount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceItem {
        private String serviceName;
        private BigDecimal unitPrice;
        private int quantity;
        private BigDecimal subtotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineItem {
        private String medicineName;
        private String unit;
        private BigDecimal unitPrice;
        private int quantity;
        private BigDecimal subtotal;
    }
}
