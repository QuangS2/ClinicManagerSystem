package com.clinicmanager.presentation.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseTrendResponse {
    private String diseaseName;
    private long caseCount;
    private double percentage;
}
