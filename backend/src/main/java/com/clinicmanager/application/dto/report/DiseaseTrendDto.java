package com.clinicmanager.application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseTrendDto {
    private String diseaseName;
    private long caseCount;
    private double percentage;
}
