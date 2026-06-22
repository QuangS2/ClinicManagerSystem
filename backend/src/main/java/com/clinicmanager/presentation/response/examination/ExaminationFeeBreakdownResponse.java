package com.clinicmanager.presentation.response.examination;

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
public class ExaminationFeeBreakdownResponse {
    private UUID medicalSlipId;
    private UUID patientId;
    private String patientName;
    private ServiceItemResponse examinationService;
    private List<ServiceItemResponse> labTests;
    private List<MedicineItemResponse> medicines;
    private BigDecimal totalAmount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceItemResponse {
        private String serviceName;
        private BigDecimal unitPrice;
        private int quantity;
        private BigDecimal subtotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineItemResponse {
        private String medicineName;
        private String unit;
        private BigDecimal unitPrice;
        private int quantity;
        private BigDecimal subtotal;
    }
}
