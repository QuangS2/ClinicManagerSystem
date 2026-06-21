package com.clinicmanager.application.port.input.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.PerformLabTestRequest;
import java.util.UUID;

public interface PerformLabTestUseCase {
    LabTestDto perform(UUID labTestId, PerformLabTestRequest request);
}
