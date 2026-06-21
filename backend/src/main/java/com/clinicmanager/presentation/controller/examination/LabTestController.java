package com.clinicmanager.presentation.controller.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.OrderLabTestsRequest;
import com.clinicmanager.application.dto.examination.PerformLabTestRequest;
import com.clinicmanager.application.dto.examination.UpdateLabTestResultRequest;
import com.clinicmanager.application.port.input.examination.OrderLabTestsUseCase;
import com.clinicmanager.application.port.input.examination.PerformLabTestUseCase;
import com.clinicmanager.application.port.input.examination.UpdateLabTestResultUseCase;
import com.clinicmanager.presentation.request.examination.SubmitLabTestOrdersRequest;
import com.clinicmanager.presentation.request.examination.SubmitPerformLabTestRequest;
import com.clinicmanager.presentation.request.examination.SubmitUpdateLabTestResultRequest;
import com.clinicmanager.presentation.response.ApiResponse;
import com.clinicmanager.presentation.response.examination.LabTestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/examinations")
@RequiredArgsConstructor
public class LabTestController {

    private final OrderLabTestsUseCase orderLabTestsUseCase;
    private final PerformLabTestUseCase performLabTestUseCase;
    private final UpdateLabTestResultUseCase updateLabTestResultUseCase;

    @PostMapping("/{medicalSlipId}/lab-tests")
    public ApiResponse<List<LabTestResponse>> orderLabTests(
            @PathVariable UUID medicalSlipId,
            @Valid @RequestBody SubmitLabTestOrdersRequest request) {

        OrderLabTestsRequest appRequest = OrderLabTestsRequest.builder()
                .testTypes(request.getTestTypes())
                .build();

        List<LabTestDto> resultDtos = orderLabTestsUseCase.order(medicalSlipId, appRequest);

        List<LabTestResponse> response = resultDtos.stream()
                .map(dto -> LabTestResponse.builder()
                        .id(dto.getId())
                        .testType(dto.getTestType())
                        .result(dto.getResult())
                        .testDate(dto.getTestDate())
                        .medicalSlipId(dto.getMedicalSlipId())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(response, "Chỉ định xét nghiệm thành công.");
    }

    @PutMapping("/lab-tests/{labTestId}/perform")
    public ApiResponse<LabTestResponse> performLabTest(
            @PathVariable UUID labTestId,
            @Valid @RequestBody SubmitPerformLabTestRequest request) {

        PerformLabTestRequest appRequest = PerformLabTestRequest.builder()
                .testDate(request.getTestDate())
                .build();

        LabTestDto resultDto = performLabTestUseCase.perform(labTestId, appRequest);

        LabTestResponse response = LabTestResponse.builder()
                .id(resultDto.getId())
                .testType(resultDto.getTestType())
                .result(resultDto.getResult())
                .testDate(resultDto.getTestDate())
                .medicalSlipId(resultDto.getMedicalSlipId())
                .build();

        return ApiResponse.success(response, "Thực hiện xét nghiệm thành công.");
    }

    @PutMapping("/lab-tests/{labTestId}/result")
    public ApiResponse<LabTestResponse> updateLabTestResult(
            @PathVariable UUID labTestId,
            @Valid @RequestBody SubmitUpdateLabTestResultRequest request) {

        UpdateLabTestResultRequest appRequest = UpdateLabTestResultRequest.builder()
                .result(request.getResult())
                .build();

        LabTestDto resultDto = updateLabTestResultUseCase.updateResult(labTestId, appRequest);

        LabTestResponse response = LabTestResponse.builder()
                .id(resultDto.getId())
                .testType(resultDto.getTestType())
                .result(resultDto.getResult())
                .testDate(resultDto.getTestDate())
                .medicalSlipId(resultDto.getMedicalSlipId())
                .build();

        return ApiResponse.success(response, "Cập nhật kết quả xét nghiệm thành công.");
    }
}
