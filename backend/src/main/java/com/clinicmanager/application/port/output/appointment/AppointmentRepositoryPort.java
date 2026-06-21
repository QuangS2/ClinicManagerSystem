package com.clinicmanager.application.port.output.appointment;

import com.clinicmanager.domain.model.appointment.Appointment;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepositoryPort {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(UUID id);
}
