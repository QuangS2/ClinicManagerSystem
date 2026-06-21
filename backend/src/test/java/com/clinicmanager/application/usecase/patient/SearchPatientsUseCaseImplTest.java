package com.clinicmanager.application.usecase.patient;

import com.clinicmanager.application.dto.patient.PatientDto;
import com.clinicmanager.application.mapper.patient.PatientMapper;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.domain.model.patient.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchPatientsUseCaseImplTest {

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private SearchPatientsUseCaseImpl searchPatientsUseCase;

    private Patient patient1;
    private PatientDto patientDto1;

    @BeforeEach
    void setUp() {
        patient1 = new Patient(
                UUID.randomUUID(),
                "Nguyễn Văn A",
                LocalDate.of(1990, 1, 1),
                "Nam",
                "0987654321",
                "Hà Nội",
                "vana@example.com"
        );

        patientDto1 = PatientDto.builder()
                .id(patient1.getId())
                .fullName("Nguyễn Văn A")
                .dob(LocalDate.of(1990, 1, 1))
                .gender("Nam")
                .phone("0987654321")
                .address("Hà Nội")
                .email("vana@example.com")
                .build();
    }

    @Test
    void search_WithValidCriteria_ReturnsList() {
        // Arrange
        String name = "Nguyễn";
        String phone = "0987";
        when(patientRepositoryPort.search(name, phone)).thenReturn(List.of(patient1));
        when(patientMapper.toDto(patient1)).thenReturn(patientDto1);

        // Act
        List<PatientDto> result = searchPatientsUseCase.search(name, phone);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(patientDto1.getFullName(), result.get(0).getFullName());
        verify(patientRepositoryPort, times(1)).search(name, phone);
    }

    @Test
    void search_WithNullCriteria_NormalizesToEmptyStrings() {
        // Arrange
        when(patientRepositoryPort.search("", "")).thenReturn(Collections.emptyList());

        // Act
        List<PatientDto> result = searchPatientsUseCase.search(null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepositoryPort, times(1)).search("", "");
    }

    @Test
    void search_LimitsTo100Results() {
        // Arrange
        List<Patient> largeList = new ArrayList<>();
        for (int i = 0; i < 105; i++) {
            largeList.add(patient1);
        }
        when(patientRepositoryPort.search("Nguyễn", "0987")).thenReturn(largeList);
        when(patientMapper.toDto(any(Patient.class))).thenReturn(patientDto1);

        // Act
        List<PatientDto> result = searchPatientsUseCase.search("Nguyễn", "0987");

        // Assert
        assertNotNull(result);
        assertEquals(100, result.size()); // Limited to 100
        verify(patientRepositoryPort, times(1)).search("Nguyễn", "0987");
        verify(patientMapper, times(100)).toDto(any(Patient.class));
    }
}
