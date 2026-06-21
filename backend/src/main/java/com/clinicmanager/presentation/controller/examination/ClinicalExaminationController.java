package com.clinicmanager.presentation.controller.examination;

import com.clinicmanager.application.dto.admission.MedicalSlipDto;
import com.clinicmanager.application.dto.examination.PerformClinicalExamRequest;
import com.clinicmanager.application.port.input.examination.PerformClinicalExamUseCase;
import com.clinicmanager.presentation.request.examination.SubmitClinicalExamRequest;
import com.clinicmanager.presentation.response.ApiResponse;
import com.clinicmanager.presentation.response.admission.MedicalSlipResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/examinations")
@RequiredArgsConstructor
public class ClinicalExaminationController {

    private final PerformClinicalExamUseCase performClinicalExamUseCase;

    @PutMapping("/{medicalSlipId}/clinical")
    public ApiResponse<MedicalSlipResponse> performClinicalExam(
            @PathVariable UUID medicalSlipId,
            @Valid @RequestBody SubmitClinicalExamRequest request) {

        PerformClinicalExamRequest appRequest = PerformClinicalExamRequest.builder()
                .symptoms(request.getSymptoms())
                .pulse(request.getPulse())
                .temperature(request.getTemperature())
                .bloodPressure(request.getBloodPressure())
                .weight(request.getWeight())
                .height(request.getHeight())
                .build();

        MedicalSlipDto resultDto = performClinicalExamUseCase.perform(medicalSlipId, appRequest);

        MedicalSlipResponse response = MedicalSlipResponse.builder()
                .id(resultDto.getId())
                .examinationDate(resultDto.getExaminationDate())
                .status(resultDto.getStatus())
                .patientId(resultDto.getPatientId())
                .symptoms(resultDto.getSymptoms())
                .pulse(resultDto.getPulse())
                .temperature(resultDto.getTemperature())
                .bloodPressure(resultDto.getBloodPressure())
                .weight(resultDto.getWeight())
                .height(resultDto.getHeight())
                .build();

        return ApiResponse.success(response, "Khám lâm sàng thành công.");
    }
}
