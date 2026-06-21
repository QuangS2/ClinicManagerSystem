package com.clinicmanager.presentation.controller.appointment;

import com.clinicmanager.application.dto.appointment.AppointmentDto;
import com.clinicmanager.application.dto.appointment.BookAppointmentRequest;
import com.clinicmanager.application.port.input.appointment.BookAppointmentUseCase;
import com.clinicmanager.application.port.input.appointment.CancelAppointmentUseCase;
import com.clinicmanager.presentation.request.appointment.CreateAppointmentRequest;
import com.clinicmanager.presentation.response.ApiResponse;
import com.clinicmanager.presentation.response.appointment.AppointmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final BookAppointmentUseCase bookAppointmentUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AppointmentResponse> bookAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        BookAppointmentRequest appRequest = BookAppointmentRequest.builder()
                .patientId(request.getPatientId())
                .serviceId(request.getServiceId())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .build();

        AppointmentDto appointmentDto = bookAppointmentUseCase.book(appRequest);

        AppointmentResponse response = AppointmentResponse.builder()
                .id(appointmentDto.getId())
                .patientId(appointmentDto.getPatientId())
                .serviceId(appointmentDto.getServiceId())
                .appointmentDate(appointmentDto.getAppointmentDate())
                .appointmentTime(appointmentDto.getAppointmentTime())
                .status(appointmentDto.getStatus())
                .build();

        return ApiResponse.success(response, "Đặt lịch hẹn thành công.");
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancelAppointment(@PathVariable UUID id) {
        cancelAppointmentUseCase.cancel(id);
        return ApiResponse.success(null, "Hủy lịch hẹn thành công.");
    }
}
