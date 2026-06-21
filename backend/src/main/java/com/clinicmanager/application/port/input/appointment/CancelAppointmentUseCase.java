package com.clinicmanager.application.port.input.appointment;

import java.util.UUID;

public interface CancelAppointmentUseCase {
    void cancel(UUID appointmentId);
}
