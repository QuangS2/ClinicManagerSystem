package com.clinicmanager.presentation.controller.examination;

import com.clinicmanager.application.dto.examination.ExaminationFeeBreakdownDto;
import com.clinicmanager.application.port.input.examination.CalculateExaminationFeeUseCase;
import com.clinicmanager.presentation.response.ApiResponse;
import com.clinicmanager.presentation.response.examination.ExaminationFeeBreakdownResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/examinations")
@RequiredArgsConstructor
public class ExaminationFeeController {

    private final CalculateExaminationFeeUseCase calculateExaminationFeeUseCase;

    @GetMapping("/{medicalSlipId}/fee-breakdown")
    public ApiResponse<ExaminationFeeBreakdownResponse> calculateFee(@PathVariable UUID medicalSlipId) {
        ExaminationFeeBreakdownDto dto = calculateExaminationFeeUseCase.calculate(medicalSlipId);

        ExaminationFeeBreakdownResponse.ServiceItemResponse examService = ExaminationFeeBreakdownResponse.ServiceItemResponse.builder()
                .serviceName(dto.getExaminationService().getServiceName())
                .unitPrice(dto.getExaminationService().getUnitPrice())
                .quantity(dto.getExaminationService().getQuantity())
                .subtotal(dto.getExaminationService().getSubtotal())
                .build();

        List<ExaminationFeeBreakdownResponse.ServiceItemResponse> labTests = dto.getLabTests().stream()
                .map(item -> ExaminationFeeBreakdownResponse.ServiceItemResponse.builder()
                        .serviceName(item.getServiceName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        List<ExaminationFeeBreakdownResponse.MedicineItemResponse> medicines = dto.getMedicines().stream()
                .map(item -> ExaminationFeeBreakdownResponse.MedicineItemResponse.builder()
                        .medicineName(item.getMedicineName())
                        .unit(item.getUnit())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        ExaminationFeeBreakdownResponse response = ExaminationFeeBreakdownResponse.builder()
                .medicalSlipId(dto.getMedicalSlipId())
                .patientId(dto.getPatientId())
                .patientName(dto.getPatientName())
                .examinationService(examService)
                .labTests(labTests)
                .medicines(medicines)
                .totalAmount(dto.getTotalAmount())
                .build();

        return ApiResponse.success(response, "Tính chi phí khám thành công.");
    }
}
