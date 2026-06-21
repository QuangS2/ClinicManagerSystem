package com.clinicmanager.application.dto.admission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdmissionRequest {
    private UUID patientId;
    private UUID appointmentId; // Nullable for walk-ins
}
