package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.PerformLabTestRequest;
import com.clinicmanager.application.mapper.examination.LabTestMapper;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.examination.LabTestRepositoryPort;
import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
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
class PerformLabTestUseCaseImplTest {

    @Mock
    private LabTestRepositoryPort labTestRepositoryPort;

    @Mock
    private MedicalSlipRepositoryPort medicalSlipRepositoryPort;

    @Mock
    private LabTestMapper labTestMapper;

    @InjectMocks
    private PerformLabTestUseCaseImpl performLabTestUseCase;

    private UUID labTestId;
    private UUID medicalSlipId;
    private LabTest labTest;
    private MedicalSlip examiningSlip;
    private MedicalSlip waitingSlip;
    private PerformLabTestRequest request;

    @BeforeEach
    void setUp() {
        labTestId = UUID.randomUUID();
        medicalSlipId = UUID.randomUUID();

        labTest = new LabTest(labTestId, "Xét nghiệm máu", null, null, medicalSlipId);

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

        request = PerformLabTestRequest.builder()
                .testDate(LocalDate.now())
                .build();
    }

    @Test
    void perform_Success() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.of(labTest));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(examiningSlip));
        when(labTestRepositoryPort.save(any(LabTest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LabTestDto expectedDto = LabTestDto.builder()
                .id(labTestId)
                .testType("Xét nghiệm máu")
                .testDate(LocalDate.now())
                .medicalSlipId(medicalSlipId)
                .build();
        when(labTestMapper.toDto(any(LabTest.class))).thenReturn(expectedDto);

        LabTestDto result = performLabTestUseCase.perform(labTestId, request);

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getTestDate());
        verify(labTestRepositoryPort, times(1)).save(any(LabTest.class));
    }

    @Test
    void perform_ThrowsLabTestNotFoundException_WhenTestDoesNotExist() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.empty());

        assertThrows(LabTestNotFoundException.class, () -> performLabTestUseCase.perform(labTestId, request));

        verify(labTestRepositoryPort, never()).save(any(LabTest.class));
    }

    @Test
    void perform_ThrowsMedicalSlipNotFoundException_WhenSlipDoesNotExist() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.of(labTest));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.empty());

        assertThrows(MedicalSlipNotFoundException.class, () -> performLabTestUseCase.perform(labTestId, request));

        verify(labTestRepositoryPort, never()).save(any(LabTest.class));
    }

    @Test
    void perform_ThrowsInvalidMedicalSlipDataException_WhenSlipIsNotExamining() {
        when(labTestRepositoryPort.findById(labTestId)).thenReturn(Optional.of(labTest));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(waitingSlip));

        assertThrows(InvalidMedicalSlipDataException.class, () -> performLabTestUseCase.perform(labTestId, request));

        verify(labTestRepositoryPort, never()).save(any(LabTest.class));
    }
}
