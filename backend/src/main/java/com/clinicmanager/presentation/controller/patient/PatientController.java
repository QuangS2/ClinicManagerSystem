package com.clinicmanager.presentation.controller.patient;

import com.clinicmanager.application.dto.patient.PatientDto;
import com.clinicmanager.application.dto.patient.RegisterPatientRequest;
import com.clinicmanager.application.port.input.patient.RegisterPatientUseCase;
import com.clinicmanager.application.port.input.patient.UpdatePatientUseCase;
import com.clinicmanager.application.port.input.patient.SearchPatientsUseCase;
import com.clinicmanager.presentation.request.patient.CreatePatientRequest;
import com.clinicmanager.presentation.request.patient.UpdatePatientRequest;
import com.clinicmanager.presentation.response.ApiResponse;
import com.clinicmanager.presentation.response.patient.PatientResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final RegisterPatientUseCase registerPatientUseCase;
    private final UpdatePatientUseCase updatePatientUseCase;
    private final SearchPatientsUseCase searchPatientsUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PatientResponse> registerPatient(@Valid @RequestBody CreatePatientRequest request) {
        RegisterPatientRequest appRequest = RegisterPatientRequest.builder()
            .fullName(request.getFullName())
            .dob(request.getDob())
            .gender(request.getGender())
            .phone(request.getPhone())
            .address(request.getAddress())
            .email(request.getEmail())
            .build();

        PatientDto patientDto = registerPatientUseCase.register(appRequest);

        PatientResponse response = PatientResponse.builder()
            .id(patientDto.getId())
            .fullName(patientDto.getFullName())
            .dob(patientDto.getDob())
            .gender(patientDto.getGender())
            .phone(patientDto.getPhone())
            .address(patientDto.getAddress())
            .email(patientDto.getEmail())
            .build();

        return ApiResponse.success(response, "Đăng ký bệnh nhân thành công.");
    }

    @PutMapping("/{id}")
    public ApiResponse<PatientResponse> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientRequest request) {

        com.clinicmanager.application.dto.patient.UpdatePatientRequest appRequest =
                com.clinicmanager.application.dto.patient.UpdatePatientRequest.builder()
                        .fullName(request.getFullName())
                        .dob(request.getDob())
                        .gender(request.getGender())
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .email(request.getEmail())
                        .build();

        PatientDto patientDto = updatePatientUseCase.update(id, appRequest);

        PatientResponse response = PatientResponse.builder()
                .id(patientDto.getId())
                .fullName(patientDto.getFullName())
                .dob(patientDto.getDob())
                .gender(patientDto.getGender())
                .phone(patientDto.getPhone())
                .address(patientDto.getAddress())
                .email(patientDto.getEmail())
                .build();

        return ApiResponse.success(response, "Cập nhật thông tin bệnh nhân thành công.");
    }

    @GetMapping
    public ApiResponse<List<PatientResponse>> searchPatients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {

        List<PatientDto> patientDtos = searchPatientsUseCase.search(name, phone);

        List<PatientResponse> responseList = patientDtos.stream()
                .map(dto -> PatientResponse.builder()
                        .id(dto.getId())
                        .fullName(dto.getFullName())
                        .dob(dto.getDob())
                        .gender(dto.getGender())
                        .phone(dto.getPhone())
                        .address(dto.getAddress())
                        .email(dto.getEmail())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(responseList, "Tra cứu bệnh nhân thành công.");
    }
}
