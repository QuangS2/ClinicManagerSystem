package com.clinicmanager.application.usecase.appointment;

import com.clinicmanager.application.dto.appointment.AppointmentDto;
import com.clinicmanager.application.dto.appointment.BookAppointmentRequest;
import com.clinicmanager.application.mapper.appointment.AppointmentMapper;
import com.clinicmanager.application.port.output.appointment.AppointmentRepositoryPort;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.application.port.output.service.MedicalServiceRepositoryPort;
import com.clinicmanager.domain.exception.patient.PatientNotFoundException;
import com.clinicmanager.domain.exception.service.MedicalServiceNotFoundException;
import com.clinicmanager.domain.exception.appointment.InvalidAppointmentDataException;
import com.clinicmanager.domain.model.appointment.Appointment;
import com.clinicmanager.domain.model.appointment.AppointmentStatus;
import com.clinicmanager.domain.model.patient.Patient;
import com.clinicmanager.domain.model.service.MedicalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookAppointmentUseCaseImplTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepositoryPort;

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    @Mock
    private MedicalServiceRepositoryPort medicalServiceRepositoryPort;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private BookAppointmentUseCaseImpl bookAppointmentUseCase;

    private UUID patientId;
    private UUID serviceId;
    private BookAppointmentRequest request;
    private Patient patient;
    private MedicalService service;
    private Appointment appointment;
    private AppointmentDto expectedDto;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        serviceId = UUID.randomUUID();

        request = BookAppointmentRequest.builder()
                .patientId(patientId)
                .serviceId(serviceId)
                .appointmentDate(LocalDate.now().plusDays(1))
                .appointmentTime(LocalTime.of(10, 0))
                .build();

        patient = new Patient(
                patientId,
                "Nguyễn Văn A",
                LocalDate.of(1990, 1, 1),
                "Nam",
                "0987654321",
                "Hà Nội",
                "vana@example.com"
        );

        service = new MedicalService(
                serviceId,
                "Khám tổng quát",
                BigDecimal.valueOf(150000),
                "Khám lâm sàng tổng quát"
        );

        appointment = new Appointment(
                UUID.randomUUID(),
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                AppointmentStatus.PENDING,
                patientId,
                serviceId
        );

        expectedDto = AppointmentDto.builder()
                .id(appointment.getId())
                .patientId(patientId)
                .serviceId(serviceId)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status("PENDING")
                .build();
    }

    @Test
    void book_Success() {
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));
        when(medicalServiceRepositoryPort.findById(serviceId)).thenReturn(Optional.of(service));
        when(appointmentRepositoryPort.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toDto(appointment)).thenReturn(expectedDto);

        AppointmentDto result = bookAppointmentUseCase.book(request);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getPatientId(), result.getPatientId());
        assertEquals(expectedDto.getServiceId(), result.getServiceId());
        assertEquals("PENDING", result.getStatus());

        verify(patientRepositoryPort, times(1)).findById(patientId);
        verify(medicalServiceRepositoryPort, times(1)).findById(serviceId);
        verify(appointmentRepositoryPort, times(1)).save(any(Appointment.class));
    }

    @Test
    void book_ThrowsPatientNotFoundException_WhenPatientDoesNotExist() {
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> bookAppointmentUseCase.book(request));

        verify(patientRepositoryPort, times(1)).findById(patientId);
        verifyNoInteractions(medicalServiceRepositoryPort, appointmentRepositoryPort, appointmentMapper);
    }

    @Test
    void book_ThrowsMedicalServiceNotFoundException_WhenServiceDoesNotExist() {
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));
        when(medicalServiceRepositoryPort.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(MedicalServiceNotFoundException.class, () -> bookAppointmentUseCase.book(request));

        verify(patientRepositoryPort, times(1)).findById(patientId);
        verify(medicalServiceRepositoryPort, times(1)).findById(serviceId);
        verifyNoInteractions(appointmentRepositoryPort, appointmentMapper);
    }

    @Test
    void book_ThrowsInvalidAppointmentDataException_WhenDateInPast() {
        request.setAppointmentDate(LocalDate.now().minusDays(1));

        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));
        when(medicalServiceRepositoryPort.findById(serviceId)).thenReturn(Optional.of(service));

        assertThrows(InvalidAppointmentDataException.class, () -> bookAppointmentUseCase.book(request));

        verify(patientRepositoryPort, times(1)).findById(patientId);
        verify(medicalServiceRepositoryPort, times(1)).findById(serviceId);
        verifyNoInteractions(appointmentRepositoryPort, appointmentMapper);
    }
}
