package com.clinicmanager.infrastructure.persistence.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaAppointmentRepository extends JpaRepository<AppointmentEntity, String> {
    List<AppointmentEntity> findByPatientIdAndAppointmentDate(String patientId, LocalDate appointmentDate);
}
