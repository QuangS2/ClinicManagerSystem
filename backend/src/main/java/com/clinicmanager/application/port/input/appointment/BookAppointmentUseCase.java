package com.clinicmanager.application.port.input.appointment;

import com.clinicmanager.application.dto.appointment.AppointmentDto;
import com.clinicmanager.application.dto.appointment.BookAppointmentRequest;

public interface BookAppointmentUseCase {
    AppointmentDto book(BookAppointmentRequest request);
}
