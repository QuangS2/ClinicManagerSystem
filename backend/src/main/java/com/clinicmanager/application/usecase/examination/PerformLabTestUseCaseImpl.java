package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.PerformLabTestRequest;
import com.clinicmanager.application.mapper.examination.LabTestMapper;
import com.clinicmanager.application.port.input.examination.PerformLabTestUseCase;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.examination.LabTestRepositoryPort;
import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
import com.clinicmanager.domain.exception.examination.LabTestNotFoundException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.examination.LabTest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PerformLabTestUseCaseImpl implements PerformLabTestUseCase {

    private final LabTestRepositoryPort labTestRepositoryPort;
    private final MedicalSlipRepositoryPort medicalSlipRepositoryPort;
    private final LabTestMapper labTestMapper;

    @Override
    @Transactional
    public LabTestDto perform(UUID labTestId, PerformLabTestRequest request) {
        LabTest labTest = labTestRepositoryPort.findById(labTestId)
                .orElseThrow(() -> new LabTestNotFoundException("Chỉ định xét nghiệm không tồn tại."));

        MedicalSlip medicalSlip = medicalSlipRepositoryPort.findById(labTest.getMedicalSlipId())
                .orElseThrow(() -> new MedicalSlipNotFoundException("Phiếu khám liên quan không tồn tại."));

        if (medicalSlip.getStatus() != MedicalSlipStatus.EXAMINING) {
            throw new InvalidMedicalSlipDataException("Chỉ có thể thực hiện xét nghiệm khi bệnh nhân đang được khám lâm sàng.");
        }

        LocalDate testDate = request.getTestDate() != null ? request.getTestDate() : LocalDate.now();
        LabTest performedTest = labTest.perform(testDate);
        
        LabTest savedTest = labTestRepositoryPort.save(performedTest);
        return labTestMapper.toDto(savedTest);
    }
}
