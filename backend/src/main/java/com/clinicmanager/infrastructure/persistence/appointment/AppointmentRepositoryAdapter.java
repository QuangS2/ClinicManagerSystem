package com.clinicmanager.infrastructure.persistence.appointment;

import com.clinicmanager.application.port.output.appointment.AppointmentRepositoryPort;
import com.clinicmanager.domain.model.appointment.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private final JpaAppointmentRepository jpaAppointmentRepository;
    private final AppointmentPersistenceMapper persistenceMapper;

    @Override
    public Appointment save(Appointment appointment) {
        AppointmentEntity entity = persistenceMapper.toEntity(appointment);
        AppointmentEntity savedEntity = jpaAppointmentRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Appointment> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return jpaAppointmentRepository.findById(id.toString()).map(persistenceMapper::toDomain);
    }
}
