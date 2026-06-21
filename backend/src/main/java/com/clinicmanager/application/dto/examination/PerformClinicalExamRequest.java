package com.clinicmanager.application.dto.examination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformClinicalExamRequest {
    private String symptoms;
    private Integer pulse;
    private Double temperature;
    private String bloodPressure;
    private Double weight;
    private Double height;
}
