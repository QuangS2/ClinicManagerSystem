package com.clinicmanager.presentation.request.appointment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {

    @NotNull(message = "Mã bệnh nhân không được để trống")
    private UUID patientId;

    @NotNull(message = "Mã dịch vụ không được để trống")
    private UUID serviceId;

    @NotNull(message = "Ngày hẹn không được để trống")
    @FutureOrPresent(message = "Ngày hẹn phải ở hiện tại hoặc tương lai")
    private LocalDate appointmentDate;

    @NotNull(message = "Giờ hẹn không được để trống")
    private LocalTime appointmentTime;
}
