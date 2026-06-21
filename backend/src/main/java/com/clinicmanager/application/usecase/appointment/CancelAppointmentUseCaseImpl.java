package com.clinicmanager.application.usecase.appointment;

import com.clinicmanager.application.port.input.appointment.CancelAppointmentUseCase;
import com.clinicmanager.application.port.output.appointment.AppointmentRepositoryPort;
import com.clinicmanager.domain.exception.appointment.AppointmentNotFoundException;
import com.clinicmanager.domain.model.appointment.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelAppointmentUseCaseImpl implements CancelAppointmentUseCase {

    private final AppointmentRepositoryPort appointmentRepositoryPort;

    @Override
    @Transactional
    public void cancel(UUID appointmentId) {
        Appointment appointment = appointmentRepositoryPort.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Lịch hẹn không tồn tại."));

        Appointment cancelledAppointment = appointment.cancel();
        appointmentRepositoryPort.save(cancelledAppointment);
    }
}
