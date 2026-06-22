package com.clinicmanager.application.port.input.examination;

import com.clinicmanager.application.dto.examination.ExaminationFeeBreakdownDto;
import java.util.UUID;

public interface CalculateExaminationFeeUseCase {
    ExaminationFeeBreakdownDto calculate(UUID medicalSlipId);
}
