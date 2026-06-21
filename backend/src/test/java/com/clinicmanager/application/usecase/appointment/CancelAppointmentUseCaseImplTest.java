package com.clinicmanager.application.usecase.appointment;

import com.clinicmanager.application.port.output.appointment.AppointmentRepositoryPort;
import com.clinicmanager.domain.exception.appointment.AppointmentNotFoundException;
import com.clinicmanager.domain.exception.appointment.InvalidAppointmentDataException;
import com.clinicmanager.domain.model.appointment.Appointment;
import com.clinicmanager.domain.model.appointment.AppointmentStatus;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelAppointmentUseCaseImplTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepositoryPort;

    @InjectMocks
    private CancelAppointmentUseCaseImpl cancelAppointmentUseCase;

    private UUID appointmentId;
    private Appointment pendingAppointment;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        pendingAppointment = new Appointment(
                appointmentId,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                AppointmentStatus.PENDING,
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    @Test
    void cancel_Success() {
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(pendingAppointment));

        cancelAppointmentUseCase.cancel(appointmentId);

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepositoryPort, times(1)).save(appointmentCaptor.capture());
        
        Appointment savedAppointment = appointmentCaptor.getValue();
        assertEquals(appointmentId, savedAppointment.getId());
        assertEquals(AppointmentStatus.CANCELLED, savedAppointment.getStatus());
    }

    @Test
    void cancel_ThrowsAppointmentNotFoundException_WhenAppointmentDoesNotExist() {
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(AppointmentNotFoundException.class, () -> cancelAppointmentUseCase.cancel(appointmentId));

        verify(appointmentRepositoryPort, never()).save(any(Appointment.class));
    }

    @Test
    void cancel_ThrowsInvalidAppointmentDataException_WhenAlreadyCancelled() {
        Appointment cancelledAppointment = new Appointment(
                appointmentId,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                AppointmentStatus.CANCELLED,
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(cancelledAppointment));

        assertThrows(InvalidAppointmentDataException.class, () -> cancelAppointmentUseCase.cancel(appointmentId));

        verify(appointmentRepositoryPort, never()).save(any(Appointment.class));
    }

    @Test
    void cancel_ThrowsInvalidAppointmentDataException_WhenCompleted() {
        Appointment completedAppointment = new Appointment(
                appointmentId,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                AppointmentStatus.COMPLETED,
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        when(appointmentRepositoryPort.findById(appointmentId)).thenReturn(Optional.of(completedAppointment));

        assertThrows(InvalidAppointmentDataException.class, () -> cancelAppointmentUseCase.cancel(appointmentId));

        verify(appointmentRepositoryPort, never()).save(any(Appointment.class));
    }
}
