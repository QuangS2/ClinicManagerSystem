package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.examination.LabTestDto;
import com.clinicmanager.application.dto.examination.OrderLabTestsRequest;
import com.clinicmanager.application.mapper.examination.LabTestMapper;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.examination.LabTestRepositoryPort;
import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderLabTestsUseCaseImplTest {

    @Mock
    private MedicalSlipRepositoryPort medicalSlipRepositoryPort;

    @Mock
    private LabTestRepositoryPort labTestRepositoryPort;

    @Mock
    private LabTestMapper labTestMapper;

    @InjectMocks
    private OrderLabTestsUseCaseImpl orderLabTestsUseCase;

    private UUID medicalSlipId;
    private MedicalSlip examiningSlip;
    private MedicalSlip waitingSlip;
    private OrderLabTestsRequest validRequest;

    @BeforeEach
    void setUp() {
        medicalSlipId = UUID.randomUUID();

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

        validRequest = OrderLabTestsRequest.builder()
                .testTypes(List.of("Xét nghiệm máu", "Xét nghiệm nước tiểu"))
                .build();
    }

    @Test
    void order_Success() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(examiningSlip));
        
        List<LabTest> mockSavedTests = List.of(
                new LabTest(UUID.randomUUID(), "Xét nghiệm máu", null, null, medicalSlipId),
                new LabTest(UUID.randomUUID(), "Xét nghiệm nước tiểu", null, null, medicalSlipId)
        );
        when(labTestRepositoryPort.saveAll(anyList())).thenReturn(mockSavedTests);

        List<LabTestDto> expectedDtos = List.of(
                LabTestDto.builder().id(mockSavedTests.get(0).getId()).testType("Xét nghiệm máu").medicalSlipId(medicalSlipId).build(),
                LabTestDto.builder().id(mockSavedTests.get(1).getId()).testType("Xét nghiệm nước tiểu").medicalSlipId(medicalSlipId).build()
        );
        when(labTestMapper.toDtoList(mockSavedTests)).thenReturn(expectedDtos);

        List<LabTestDto> result = orderLabTestsUseCase.order(medicalSlipId, validRequest);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Xét nghiệm máu", result.get(0).getTestType());
        assertEquals("Xét nghiệm nước tiểu", result.get(1).getTestType());
        verify(labTestRepositoryPort, times(1)).saveAll(anyList());
    }

    @Test
    void order_ThrowsMedicalSlipNotFoundException_WhenSlipDoesNotExist() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.empty());

        assertThrows(MedicalSlipNotFoundException.class, () -> orderLabTestsUseCase.order(medicalSlipId, validRequest));

        verify(labTestRepositoryPort, never()).saveAll(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    void order_ThrowsInvalidMedicalSlipDataException_WhenSlipIsNotExamining() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(waitingSlip));

        assertThrows(InvalidMedicalSlipDataException.class, () -> orderLabTestsUseCase.order(medicalSlipId, validRequest));

        verify(labTestRepositoryPort, never()).saveAll(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    void order_ThrowsInvalidMedicalSlipDataException_WhenTestTypesIsNull() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(examiningSlip));
        validRequest.setTestTypes(null);

        assertThrows(InvalidMedicalSlipDataException.class, () -> orderLabTestsUseCase.order(medicalSlipId, validRequest));

        verify(labTestRepositoryPort, never()).saveAll(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    void order_ThrowsInvalidMedicalSlipDataException_WhenTestTypesIsEmpty() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(examiningSlip));
        validRequest.setTestTypes(Collections.emptyList());

        assertThrows(InvalidMedicalSlipDataException.class, () -> orderLabTestsUseCase.order(medicalSlipId, validRequest));

        verify(labTestRepositoryPort, never()).saveAll(anyList());
    }
}
