package com.clinicmanager.application.port.input.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.UpdateLabTestResultRequest;
import java.util.UUID;

public interface UpdateLabTestResultUseCase {
    LabTestDto updateResult(UUID labTestId, UpdateLabTestResultRequest request);
}
