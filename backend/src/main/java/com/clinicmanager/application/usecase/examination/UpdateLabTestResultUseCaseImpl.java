package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.UpdateLabTestResultRequest;
import com.clinicmanager.application.mapper.examination.LabTestMapper;
import com.clinicmanager.application.port.input.examination.UpdateLabTestResultUseCase;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateLabTestResultUseCaseImpl implements UpdateLabTestResultUseCase {

    private final LabTestRepositoryPort labTestRepositoryPort;
    private final MedicalSlipRepositoryPort medicalSlipRepositoryPort;
    private final LabTestMapper labTestMapper;

    @Override
    @Transactional
    public LabTestDto updateResult(UUID labTestId, UpdateLabTestResultRequest request) {
        LabTest labTest = labTestRepositoryPort.findById(labTestId)
                .orElseThrow(() -> new LabTestNotFoundException("Chỉ định xét nghiệm không tồn tại."));

        MedicalSlip medicalSlip = medicalSlipRepositoryPort.findById(labTest.getMedicalSlipId())
                .orElseThrow(() -> new MedicalSlipNotFoundException("Phiếu khám liên quan không tồn tại."));

        if (medicalSlip.getStatus() != MedicalSlipStatus.EXAMINING) {
            throw new InvalidMedicalSlipDataException("Chỉ có thể cập nhật kết quả xét nghiệm khi bệnh nhân đang được khám lâm sàng.");
        }

        LabTest updatedTest = labTest.updateResult(request.getResult());
        
        LabTest savedTest = labTestRepositoryPort.save(updatedTest);
        return labTestMapper.toDto(savedTest);
    }
}
