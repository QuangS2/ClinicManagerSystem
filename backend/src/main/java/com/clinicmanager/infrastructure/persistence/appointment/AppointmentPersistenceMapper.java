package com.clinicmanager.infrastructure.persistence.appointment;

import com.clinicmanager.domain.model.appointment.Appointment;
import com.clinicmanager.domain.model.appointment.AppointmentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AppointmentPersistenceMapper {

    @Mapping(target = "id", expression = "java(domain.getId() != null ? domain.getId().toString() : null)")
    @Mapping(target = "patientId", expression = "java(domain.getPatientId() != null ? domain.getPatientId().toString() : null)")
    @Mapping(target = "serviceId", expression = "java(domain.getServiceId() != null ? domain.getServiceId().toString() : null)")
    @Mapping(target = "status", expression = "java(domain.getStatus() != null ? domain.getStatus().name() : null)")
    AppointmentEntity toEntity(Appointment domain);

    default Appointment toDomain(AppointmentEntity entity) {
        if (entity == null) {
            return null;
        }
        UUID id = entity.getId() != null ? UUID.fromString(entity.getId()) : null;
        UUID patientId = entity.getPatientId() != null ? UUID.fromString(entity.getPatientId()) : null;
        UUID serviceId = entity.getServiceId() != null ? UUID.fromString(entity.getServiceId()) : null;
        AppointmentStatus status = entity.getStatus() != null ? AppointmentStatus.valueOf(entity.getStatus()) : null;

        return new Appointment(
            id,
            entity.getAppointmentDate(),
            entity.getAppointmentTime(),
            status,
            patientId,
            serviceId
        );
    }
}
