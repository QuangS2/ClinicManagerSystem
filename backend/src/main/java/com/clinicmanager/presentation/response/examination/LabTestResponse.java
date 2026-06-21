package com.clinicmanager.presentation.response.examination;

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
public class LabTestResponse {
    private UUID id;
    private String testType;
    private String result;
    private LocalDate testDate;
    private UUID medicalSlipId;
}
