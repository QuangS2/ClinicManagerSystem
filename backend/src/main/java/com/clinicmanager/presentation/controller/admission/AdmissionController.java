package com.clinicmanager.presentation.controller.admission;

import com.clinicmanager.application.dto.admission.MedicalSlipDto;
import com.clinicmanager.application.dto.admission.RegisterAdmissionRequest;
import com.clinicmanager.application.port.input.admission.RegisterAdmissionUseCase;
import com.clinicmanager.presentation.request.admission.CreateAdmissionRequest;
import com.clinicmanager.presentation.response.ApiResponse;
import com.clinicmanager.presentation.response.admission.MedicalSlipResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admissions")
@RequiredArgsConstructor
public class AdmissionController {

    private final RegisterAdmissionUseCase registerAdmissionUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MedicalSlipResponse> registerAdmission(@Valid @RequestBody CreateAdmissionRequest request) {
        RegisterAdmissionRequest appRequest = RegisterAdmissionRequest.builder()
                .patientId(request.getPatientId())
                .appointmentId(request.getAppointmentId())
                .build();

        MedicalSlipDto slipDto = registerAdmissionUseCase.register(appRequest);

        MedicalSlipResponse response = MedicalSlipResponse.builder()
                .id(slipDto.getId())
                .examinationDate(slipDto.getExaminationDate())
                .status(slipDto.getStatus())
                .patientId(slipDto.getPatientId())
                .build();

        return ApiResponse.success(response, "Đăng ký tiếp nhận khám bệnh thành công.");
    }
}
