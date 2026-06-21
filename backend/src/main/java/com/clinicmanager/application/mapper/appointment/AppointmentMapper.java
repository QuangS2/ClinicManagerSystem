package com.clinicmanager.application.mapper.appointment;

import com.clinicmanager.application.dto.appointment.AppointmentDto;
import com.clinicmanager.application.dto.appointment.BookAppointmentRequest;
import com.clinicmanager.domain.model.appointment.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    AppointmentDto toDto(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Appointment toDomain(BookAppointmentRequest request);
}
