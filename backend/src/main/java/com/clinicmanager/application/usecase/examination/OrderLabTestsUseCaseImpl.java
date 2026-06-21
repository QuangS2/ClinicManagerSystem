package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.OrderLabTestsRequest;
import com.clinicmanager.application.mapper.examination.LabTestMapper;
import com.clinicmanager.application.port.input.examination.OrderLabTestsUseCase;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.examination.LabTestRepositoryPort;
import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.examination.LabTest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderLabTestsUseCaseImpl implements OrderLabTestsUseCase {

    private final MedicalSlipRepositoryPort medicalSlipRepositoryPort;
    private final LabTestRepositoryPort labTestRepositoryPort;
    private final LabTestMapper labTestMapper;

    @Override
    @Transactional
    public List<LabTestDto> order(UUID medicalSlipId, OrderLabTestsRequest request) {
        MedicalSlip medicalSlip = medicalSlipRepositoryPort.findById(medicalSlipId)
                .orElseThrow(() -> new MedicalSlipNotFoundException("Phiếu khám không tồn tại."));

        if (medicalSlip.getStatus() != MedicalSlipStatus.EXAMINING) {
            throw new InvalidMedicalSlipDataException("Chỉ có thể chỉ định xét nghiệm khi bệnh nhân đang được khám lâm sàng.");
        }

        if (request.getTestTypes() == null || request.getTestTypes().isEmpty()) {
            throw new InvalidMedicalSlipDataException("Danh sách xét nghiệm chỉ định không được để trống.");
        }

        List<LabTest> tests = request.getTestTypes().stream()
                .map(type -> new LabTest(null, type, null, null, medicalSlipId))
                .collect(Collectors.toList());

        List<LabTest> savedTests = labTestRepositoryPort.saveAll(tests);
        return labTestMapper.toDtoList(savedTests);
    }
}
