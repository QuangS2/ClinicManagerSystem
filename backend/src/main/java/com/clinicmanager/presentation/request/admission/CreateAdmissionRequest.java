package com.clinicmanager.presentation.request.admission;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdmissionRequest {

    @NotNull(message = "Mã bệnh nhân không được để trống")
    private UUID patientId;

    private UUID appointmentId; // Nullable for walk-ins
}
