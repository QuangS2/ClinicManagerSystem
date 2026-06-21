package com.clinicmanager.application.dto.appointment;

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
public class BookAppointmentRequest {
    private UUID patientId;
    private UUID serviceId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
}
