package com.clinicmanager.presentation.response.admission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalSlipResponse {
    private UUID id;
    private LocalDate examinationDate;
    private String status;
    private UUID patientId;
    private String symptoms;
    private Integer pulse;
    private Double temperature;
    private String bloodPressure;
    private Double weight;
    private Double height;
}
