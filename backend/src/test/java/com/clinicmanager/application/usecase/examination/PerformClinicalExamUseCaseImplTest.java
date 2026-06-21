package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.admission.MedicalSlipDto;
import com.clinicmanager.application.dto.examination.PerformClinicalExamRequest;
import com.clinicmanager.application.mapper.admission.MedicalSlipMapper;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformClinicalExamUseCaseImplTest {

    @Mock
    private MedicalSlipRepositoryPort medicalSlipRepositoryPort;

    @Mock
    private MedicalSlipMapper medicalSlipMapper;

    @InjectMocks
    private PerformClinicalExamUseCaseImpl performClinicalExamUseCase;

    private UUID medicalSlipId;
    private MedicalSlip waitingSlip;
    private PerformClinicalExamRequest validRequest;
    private MedicalSlipDto expectedDto;

    @BeforeEach
    void setUp() {
        medicalSlipId = UUID.randomUUID();

        waitingSlip = new MedicalSlip(
                medicalSlipId,
                LocalDate.now(),
                MedicalSlipStatus.WAITING,
                UUID.randomUUID(),
                null, null, null, null, null, null
        );

        validRequest = PerformClinicalExamRequest.builder()
                .symptoms("Đau đầu, sốt nhẹ")
                .pulse(80)
                .temperature(37.5)
                .bloodPressure("120/80")
                .weight(65.0)
                .height(170.0)
                .build();

        expectedDto = MedicalSlipDto.builder()
                .id(medicalSlipId)
                .examinationDate(LocalDate.now())
                .status("EXAMINING")
                .patientId(waitingSlip.getPatientId())
                .symptoms("Đau đầu, sốt nhẹ")
                .pulse(80)
                .temperature(37.5)
                .bloodPressure("120/80")
                .weight(65.0)
                .height(170.0)
                .build();
    }

    @Test
    void perform_Success() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(waitingSlip));
        when(medicalSlipRepositoryPort.save(any(MedicalSlip.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(medicalSlipMapper.toDto(any(MedicalSlip.class))).thenReturn(expectedDto);

        MedicalSlipDto result = performClinicalExamUseCase.perform(medicalSlipId, validRequest);

        assertNotNull(result);
        assertEquals("EXAMINING", result.getStatus());
        assertEquals("Đau đầu, sốt nhẹ", result.getSymptoms());
        assertEquals(80, result.getPulse());
        assertEquals(37.5, result.getTemperature());

        ArgumentCaptor<MedicalSlip> slipCaptor = ArgumentCaptor.forClass(MedicalSlip.class);
        verify(medicalSlipRepositoryPort, times(1)).save(slipCaptor.capture());
        MedicalSlip savedSlip = slipCaptor.getValue();
        assertEquals(MedicalSlipStatus.EXAMINING, savedSlip.getStatus());
        assertEquals("Đau đầu, sốt nhẹ", savedSlip.getSymptoms());
    }

    @Test
    void perform_ThrowsMedicalSlipNotFoundException_WhenSlipDoesNotExist() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.empty());

        assertThrows(MedicalSlipNotFoundException.class, () -> performClinicalExamUseCase.perform(medicalSlipId, validRequest));

        verify(medicalSlipRepositoryPort, never()).save(any(MedicalSlip.class));
    }

    @Test
    void perform_ThrowsInvalidMedicalSlipDataException_WhenSlipIsCompleted() {
        MedicalSlip completedSlip = new MedicalSlip(
                medicalSlipId,
                LocalDate.now(),
                MedicalSlipStatus.COMPLETED,
                UUID.randomUUID(),
                null, null, null, null, null, null
        );
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(completedSlip));

        assertThrows(InvalidMedicalSlipDataException.class, () -> performClinicalExamUseCase.perform(medicalSlipId, validRequest));

        verify(medicalSlipRepositoryPort, never()).save(any(MedicalSlip.class));
    }

    @Test
    void perform_ThrowsInvalidMedicalSlipDataException_WhenSlipIsCancelled() {
        MedicalSlip cancelledSlip = new MedicalSlip(
                medicalSlipId,
                LocalDate.now(),
                MedicalSlipStatus.CANCELLED,
                UUID.randomUUID(),
                null, null, null, null, null, null
        );
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(cancelledSlip));

        assertThrows(InvalidMedicalSlipDataException.class, () -> performClinicalExamUseCase.perform(medicalSlipId, validRequest));

        verify(medicalSlipRepositoryPort, never()).save(any(MedicalSlip.class));
    }

    @Test
    void perform_ThrowsInvalidMedicalSlipDataException_WhenPulseIsInvalid() {
        validRequest.setPulse(-5);
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(waitingSlip));

        assertThrows(InvalidMedicalSlipDataException.class, () -> performClinicalExamUseCase.perform(medicalSlipId, validRequest));

        verify(medicalSlipRepositoryPort, never()).save(any(MedicalSlip.class));
    }

    @Test
    void perform_ThrowsInvalidMedicalSlipDataException_WhenTemperatureIsInvalid() {
        validRequest.setTemperature(47.0);
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(waitingSlip));

        assertThrows(InvalidMedicalSlipDataException.class, () -> performClinicalExamUseCase.perform(medicalSlipId, validRequest));

        verify(medicalSlipRepositoryPort, never()).save(any(MedicalSlip.class));
    }
}
