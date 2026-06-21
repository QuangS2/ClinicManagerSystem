package com.clinicmanager.application.usecase.admission;

import com.clinicmanager.application.dto.admission.MedicalSlipDto;
import com.clinicmanager.application.dto.admission.RegisterAdmissionRequest;
import com.clinicmanager.application.mapper.admission.MedicalSlipMapper;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.appointment.AppointmentRepositoryPort;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import com.clinicmanager.domain.exception.admission.PatientAlreadyRegisteredException;
import com.clinicmanager.domain.exception.appointment.AppointmentNotFoundException;
import com.clinicmanager.domain.exception.patient.PatientNotFoundException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.appointment.Appointment;
import com.clinicmanager.domain.model.appointment.AppointmentStatus;
import com.clinicmanager.domain.model.patient.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterAdmissionUseCaseImplTest {

    @Mock
    private MedicalSlipRepositoryPort medicalSlipRepositoryPort;

    @Mock
    private AppointmentRepositoryPort appointmentRepositoryPort;

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    @Mock
    private MedicalSlipMapper medicalSlipMapper;

    @InjectMocks
    private RegisterAdmissionUseCaseImpl registerAdmissionUseCase;

    private UUID patientId;
    private UUID appointmentId;
    private Patient patient;
    private Appointment appointment;
    private MedicalSlip medicalSlip;
    private MedicalSlipDto expectedDto;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        patient = new Patient(
                patientId,
                "Nguyễn Văn B",
                LocalDate.of(1995, 5, 5),
                "Nữ",
                "0912345678",
                "Hà Nội",
                "vanb@example.com"
        );

        appointment = new Appointment(
                appointmentId,
                LocalDate.now(),
                LocalTime.of(9, 0),
                AppointmentStatus.CONFIRMED,
                patientId,
                UUID.randomUUID()
        );

        medicalSlip = new MedicalSlip(
                UUID.randomUUID(),
                LocalDate.now(),
                MedicalSlipStatus.WAITING,
                patientId,
                null, null, null, null, null, null
        );

        expectedDto = MedicalSlipDto.builder()
                .id(medicalSlip.getId())
                .examinationDate(LocalDate.now())
                .status("WAITING")
                .patientId(patientId)
                .build();
    }

    @Test
    void register_WalkIn_Success() {
        RegisterAdmissionRequest request = RegisterAdmissionRequest.builder()
                .patientId(patientId)
                .appointmentId(null)
                .build();

        when(medicalSlipRepositoryPort.existsActiveSlipByPatientIdAndDate(patientId, LocalDate.now())).thenReturn(false);
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));
        when(medicalSlipRepositoryPort.save(any(MedicalSlip.class))).thenReturn(medicalSlip);
        when(medicalSlipMapper.toDto(any(MedicalSlip.class))).thenReturn(expectedDto);

        MedicalSlipDto result = registerAdmissionUseCase.register(request);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals("WAITING", result.getStatus());

        verify(medicalSlipRepositoryPort, times(1)).existsActiveSlipByPatientIdAndDate(patientId, LocalDate.now());
        verify(patientRepositoryPort, times(1)).findById(patientId);
        verify(medicalSlipRepositoryPort, times(1)).save(any(MedicalSlip.class));
        verifyNoInteractions(appointmentRepositoryPort);
    }

    @Test
    void register_WithAppointment_Success() {
        RegisterAdmissionRequest request = RegisterAdmissionRequest.builder()
                .patientId(patientId)
                .appointmentId(appointmentId)
                .build();

        when(medicalSlipRepositoryPort.existsActiveSlipByPatientIdAndDate(patientId, LocalDate.now())).thenReturn(false);
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(medicalSlipRepositoryPort.save(any(MedicalSlip.class))).thenReturn(medicalSlip);
        when(medicalSlipMapper.toDto(any(MedicalSlip.class))).thenReturn(expectedDto);

        MedicalSlipDto result = registerAdmissionUseCase.register(request);

        assertNotNull(result);

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepositoryPort, times(1)).save(appointmentCaptor.capture());
        assertEquals(AppointmentStatus.COMPLETED, appointmentCaptor.getValue().getStatus());

        verify(medicalSlipRepositoryPort, times(1)).existsActiveSlipByPatientIdAndDate(patientId, LocalDate.now());
        verify(medicalSlipRepositoryPort, times(1)).save(any(MedicalSlip.class));
    }

    @Test
    void register_ThrowsPatientNotFoundException_WhenWalkInPatientDoesNotExist() {
        RegisterAdmissionRequest request = RegisterAdmissionRequest.builder()
                .patientId(patientId)
                .appointmentId(null)
                .build();

        when(medicalSlipRepositoryPort.existsActiveSlipByPatientIdAndDate(patientId, LocalDate.now())).thenReturn(false);
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> registerAdmissionUseCase.register(request));

        verify(medicalSlipRepositoryPort, never()).save(any(MedicalSlip.class));
    }

    @Test
    void register_ThrowsPatientAlreadyRegisteredException_WhenPatientAlreadyHasSlipToday() {
        RegisterAdmissionRequest request = RegisterAdmissionRequest.builder()
                .patientId(patientId)
                .appointmentId(null)
                .build();

        when(medicalSlipRepositoryPort.existsActiveSlipByPatientIdAndDate(patientId, LocalDate.now())).thenReturn(true);

        assertThrows(PatientAlreadyRegisteredException.class, () -> registerAdmissionUseCase.register(request));

        verify(patientRepositoryPort, never()).findById(any(UUID.class));
        verify(medicalSlipRepositoryPort, never()).save(any(MedicalSlip.class));
    }

    @Test
    void register_ThrowsInvalidMedicalSlipDataException_WhenAppointmentPatientIdDoesNotMatch() {
        UUID wrongPatientId = UUID.randomUUID();
        RegisterAdmissionRequest request = RegisterAdmissionRequest.builder()
                .patientId(wrongPatientId)
                .appointmentId(appointmentId)
                .build();

        when(medicalSlipRepositoryPort.existsActiveSlipByPatientIdAndDate(wrongPatientId, LocalDate.now())).thenReturn(false);
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(InvalidMedicalSlipDataException.class, () -> registerAdmissionUseCase.register(request));

        verify(appointmentRepositoryPort, never()).save(any(Appointment.class));
        verify(medicalSlipRepositoryPort, never()).save(any(MedicalSlip.class));
    }

    @Test
    void register_ThrowsInvalidMedicalSlipDataException_WhenAppointmentNotForToday() {
        Appointment pastAppointment = new Appointment(
                appointmentId,
                LocalDate.now().plusDays(2),
                LocalTime.of(9, 0),
                AppointmentStatus.CONFIRMED,
                patientId,
                UUID.randomUUID()
        );
        RegisterAdmissionRequest request = RegisterAdmissionRequest.builder()
                .patientId(patientId)
                .appointmentId(appointmentId)
                .build();

        when(medicalSlipRepositoryPort.existsActiveSlipByPatientIdAndDate(patientId, LocalDate.now())).thenReturn(false);
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(pastAppointment));

        assertThrows(InvalidMedicalSlipDataException.class, () -> registerAdmissionUseCase.register(request));

        verify(appointmentRepositoryPort, never()).save(any(Appointment.class));
        verify(medicalSlipRepositoryPort, never()).save(any(MedicalSlip.class));
    }
}
