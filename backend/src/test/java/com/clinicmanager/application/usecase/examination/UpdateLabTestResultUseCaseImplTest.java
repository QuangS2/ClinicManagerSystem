package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.UpdateLabTestResultRequest;
import com.clinicmanager.application.mapper.examination.LabTestMapper;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.examination.LabTestRepositoryPort;
import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
import com.clinicmanager.domain.exception.examination.InvalidLabTestDataException;
import com.clinicmanager.domain.exception.examination.LabTestNotFoundException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.examination.LabTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class UpdateLabTestResultUseCaseImplTest {

    @Mock
    private LabTestRepositoryPort labTestRepositoryPort;

    @Mock
    private MedicalSlipRepositoryPort medicalSlipRepositoryPort;

    @Mock
    private LabTestMapper labTestMapper;

    @InjectMocks
    private UpdateLabTestResultUseCaseImpl updateLabTestResultUseCase;

    private UUID labTestId;
    private UUID medicalSlipId;
    private LabTest performedTest;
    private LabTest notPerformedTest;
    private MedicalSlip examiningSlip;
    private MedicalSlip waitingSlip;
    private UpdateLabTestResultRequest request;

    @BeforeEach
    void setUp() {
        labTestId = UUID.randomUUID();
        medicalSlipId = UUID.randomUUID();

        performedTest = new LabTest(labTestId, "Xét nghiệm máu", null, LocalDate.now(), medicalSlipId);
        notPerformedTest = new LabTest(labTestId, "Xét nghiệm máu", null, null, medicalSlipId);

        examiningSlip = new MedicalSlip(
                medicalSlipId,
                LocalDate.now(),
                MedicalSlipStatus.EXAMINING,
                UUID.randomUUID(),
                null, null, null, null, null, null
        );

        waitingSlip = new MedicalSlip(
                medicalSlipId,
                LocalDate.now(),
                MedicalSlipStatus.WAITING,
                UUID.randomUUID(),
                null, null, null, null, null, null
        );

        request = UpdateLabTestResultRequest.builder()
                .result("Hồng cầu bình thường, bạch cầu hơi cao")
                .build();
    }

    @Test
    void updateResult_Success() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.of(performedTest));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(examiningSlip));
        when(labTestRepositoryPort.save(any(LabTest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LabTestDto expectedDto = LabTestDto.builder()
                .id(labTestId)
                .testType("Xét nghiệm máu")
                .testDate(LocalDate.now())
                .result("Hồng cầu bình thường, bạch cầu hơi cao")
                .medicalSlipId(medicalSlipId)
                .build();
        when(labTestMapper.toDto(any(LabTest.class))).thenReturn(expectedDto);

        LabTestDto result = updateLabTestResultUseCase.updateResult(labTestId, request);

        assertNotNull(result);
        assertEquals("Hồng cầu bình thường, bạch cầu hơi cao", result.getResult());
        verify(labTestRepositoryPort, times(1)).save(any(LabTest.class));
    }

    @Test
    void updateResult_ThrowsLabTestNotFoundException_WhenTestDoesNotExist() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.empty());

        assertThrows(LabTestNotFoundException.class, () -> updateLabTestResultUseCase.updateResult(labTestId, request));

        verify(labTestRepositoryPort, never()).save(any(LabTest.class));
    }

    @Test
    void updateResult_ThrowsMedicalSlipNotFoundException_WhenSlipDoesNotExist() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.of(performedTest));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.empty());

        assertThrows(MedicalSlipNotFoundException.class, () -> updateLabTestResultUseCase.updateResult(labTestId, request));

        verify(labTestRepositoryPort, never()).save(any(LabTest.class));
    }

    @Test
    void updateResult_ThrowsInvalidMedicalSlipDataException_WhenSlipIsNotExamining() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.of(performedTest));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(waitingSlip));

        assertThrows(InvalidMedicalSlipDataException.class, () -> updateLabTestResultUseCase.updateResult(labTestId, request));

        verify(labTestRepositoryPort, never()).save(any(LabTest.class));
    }

    @Test
    void updateResult_ThrowsInvalidLabTestDataException_WhenTestNotPerformedYet() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.of(notPerformedTest));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(examiningSlip));

        assertThrows(InvalidLabTestDataException.class, () -> updateLabTestResultUseCase.updateResult(labTestId, request));

        verify(labTestRepositoryPort, never()).save(any(LabTest.class));
    }

    @Test
    void updateResult_ThrowsInvalidLabTestDataException_WhenResultIsEmpty() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.of(performedTest));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(examiningSlip));
        request.setResult("");

        assertThrows(InvalidLabTestDataException.class, () -> updateLabTestResultUseCase.updateResult(labTestId, request));

        verify(labTestRepositoryPort, never()).save(any(LabTest.class));
    }
}
