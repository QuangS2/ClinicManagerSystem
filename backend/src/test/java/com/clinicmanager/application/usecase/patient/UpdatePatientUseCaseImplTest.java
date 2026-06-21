package com.clinicmanager.application.usecase.patient;

import com.clinicmanager.application.dto.patient.PatientDto;
import com.clinicmanager.application.dto.patient.UpdatePatientRequest;
import com.clinicmanager.application.mapper.patient.PatientMapper;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.domain.exception.patient.PatientAlreadyExistsException;
import com.clinicmanager.domain.exception.patient.PatientNotFoundException;
import com.clinicmanager.domain.model.patient.Patient;
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
class UpdatePatientUseCaseImplTest {

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private UpdatePatientUseCaseImpl updatePatientUseCase;

    private UUID patientId;
    private UpdatePatientRequest validRequest;
    private Patient existingPatient;
    private Patient updatedPatient;
    private PatientDto expectedDto;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();

        validRequest = UpdatePatientRequest.builder()
                .fullName("Nguyễn Văn B")
                .dob(LocalDate.of(1991, 2, 2))
                .gender("Nam")
                .phone("0987654322")
                .address("Hồ Chí Minh")
                .email("vanb@example.com")
                .build();

        existingPatient = new Patient(
                patientId,
                "Nguyễn Văn A",
                LocalDate.of(1990, 1, 1),
                "Nam",
                "0987654321",
                "Hà Nội",
                "vana@example.com"
        );

        updatedPatient = new Patient(
                patientId,
                "Nguyễn Văn B",
                LocalDate.of(1991, 2, 2),
                "Nam",
                "0987654322",
                "Hồ Chí Minh",
                "vanb@example.com"
        );

        expectedDto = PatientDto.builder()
                .id(patientId)
                .fullName("Nguyễn Văn B")
                .dob(LocalDate.of(1991, 2, 2))
                .gender("Nam")
                .phone("0987654322")
                .address("Hồ Chí Minh")
                .email("vanb@example.com")
                .build();
    }

    @Test
    void update_Success() {
        // Arrange
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepositoryPort.findByPhone(validRequest.getPhone())).thenReturn(Optional.empty());
        when(patientRepositoryPort.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());
        when(patientRepositoryPort.save(any(Patient.class))).thenReturn(updatedPatient);
        when(patientMapper.toDto(updatedPatient)).thenReturn(expectedDto);

        // Act
        PatientDto result = updatePatientUseCase.update(patientId, validRequest);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getFullName(), result.getFullName());
        assertEquals(expectedDto.getPhone(), result.getPhone());
        assertEquals(expectedDto.getEmail(), result.getEmail());

        verify(patientRepositoryPort, times(1)).findById(patientId);
        verify(patientRepositoryPort, times(1)).findByPhone(validRequest.getPhone());
        verify(patientRepositoryPort, times(1)).findByEmail(validRequest.getEmail());
        verify(patientRepositoryPort, times(1)).save(any(Patient.class));
    }

    @Test
    void update_ThrowsException_WhenPatientNotFound() {
        // Arrange
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        PatientNotFoundException exception = assertThrows(
                PatientNotFoundException.class,
                () -> updatePatientUseCase.update(patientId, validRequest)
        );

        assertEquals("Không tìm thấy bệnh nhân với ID: " + patientId, exception.getMessage());
        verify(patientRepositoryPort, times(1)).findById(patientId);
        verify(patientRepositoryPort, never()).findByPhone(anyString());
        verify(patientRepositoryPort, never()).save(any(Patient.class));
    }

    @Test
    void update_ThrowsException_WhenPhoneConflict() {
        // Arrange
        UUID otherId = UUID.randomUUID();
        Patient otherPatient = new Patient(
                otherId,
                "Trần Thị C",
                LocalDate.of(1995, 5, 5),
                "Nữ",
                "0987654322",
                "Đà Nẵng",
                "tranc@example.com"
        );

        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepositoryPort.findByPhone(validRequest.getPhone())).thenReturn(Optional.of(otherPatient));

        // Act & Assert
        PatientAlreadyExistsException exception = assertThrows(
                PatientAlreadyExistsException.class,
                () -> updatePatientUseCase.update(patientId, validRequest)
        );

        assertEquals("Số điện thoại đã tồn tại ở một bệnh nhân khác.", exception.getMessage());
        verify(patientRepositoryPort, times(1)).findById(patientId);
        verify(patientRepositoryPort, times(1)).findByPhone(validRequest.getPhone());
        verify(patientRepositoryPort, never()).findByEmail(anyString());
        verify(patientRepositoryPort, never()).save(any(Patient.class));
    }

    @Test
    void update_ThrowsException_WhenEmailConflict() {
        // Arrange
        UUID otherId = UUID.randomUUID();
        Patient otherPatient = new Patient(
                otherId,
                "Trần Thị C",
                LocalDate.of(1995, 5, 5),
                "Nữ",
                "0987654325",
                "Đà Nẵng",
                "vanb@example.com"
        );

        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepositoryPort.findByPhone(validRequest.getPhone())).thenReturn(Optional.empty());
        when(patientRepositoryPort.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(otherPatient));

        // Act & Assert
        PatientAlreadyExistsException exception = assertThrows(
                PatientAlreadyExistsException.class,
                () -> updatePatientUseCase.update(patientId, validRequest)
        );

        assertEquals("Email đã tồn tại ở một bệnh nhân khác.", exception.getMessage());
        verify(patientRepositoryPort, times(1)).findById(patientId);
        verify(patientRepositoryPort, times(1)).findByPhone(validRequest.getPhone());
        verify(patientRepositoryPort, times(1)).findByEmail(validRequest.getEmail());
        verify(patientRepositoryPort, never()).save(any(Patient.class));
    }
}
