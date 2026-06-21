package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.admission.MedicalSlipDto;
import com.clinicmanager.application.dto.examination.PerformClinicalExamRequest;
import com.clinicmanager.application.mapper.admission.MedicalSlipMapper;
import com.clinicmanager.application.port.input.examination.PerformClinicalExamUseCase;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PerformClinicalExamUseCaseImpl implements PerformClinicalExamUseCase {

    private final MedicalSlipRepositoryPort medicalSlipRepositoryPort;
    private final MedicalSlipMapper medicalSlipMapper;

    @Override
    @Transactional
    public MedicalSlipDto perform(UUID medicalSlipId, PerformClinicalExamRequest request) {
        MedicalSlip medicalSlip = medicalSlipRepositoryPort.findById(medicalSlipId)
                .orElseThrow(() -> new MedicalSlipNotFoundException("Phiếu khám không tồn tại."));

        MedicalSlip updatedSlip = medicalSlip.startExamination(
                request.getSymptoms(),
                request.getPulse(),
                request.getTemperature(),
                request.getBloodPressure(),
                request.getWeight(),
                request.getHeight()
        );

        MedicalSlip savedSlip = medicalSlipRepositoryPort.save(updatedSlip);
        return medicalSlipMapper.toDto(savedSlip);
    }
}
