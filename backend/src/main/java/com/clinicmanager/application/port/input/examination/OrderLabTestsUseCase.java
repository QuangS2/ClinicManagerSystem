package com.clinicmanager.application.port.input.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.OrderLabTestsRequest;
import java.util.List;
import java.util.UUID;

public interface OrderLabTestsUseCase {
    List<LabTestDto> order(UUID medicalSlipId, OrderLabTestsRequest request);
}
