package com.clinicmanager.domain.model.appointment;

import com.clinicmanager.domain.exception.appointment.InvalidAppointmentDataException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Appointment {
    private final UUID id;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private final AppointmentStatus status;
    private final UUID patientId;
    private final UUID serviceId;

    public Appointment(UUID id, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status, UUID patientId, UUID serviceId) {
        this.id = id != null ? id : UUID.randomUUID();
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status != null ? status : AppointmentStatus.PENDING;
        this.patientId = patientId;
        this.serviceId = serviceId;
        validate();
    }

    private void validate() {
        if (appointmentDate == null) {
            throw new InvalidAppointmentDataException("Ngày hẹn không được để trống.");
        }
        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new InvalidAppointmentDataException("Ngày hẹn không được ở quá khứ.");
        }
        if (appointmentTime == null) {
            throw new InvalidAppointmentDataException("Giờ hẹn không được để trống.");
        }
        if (patientId == null) {
            throw new InvalidAppointmentDataException("Mã bệnh nhân không được để trống.");
        }
        if (serviceId == null) {
            throw new InvalidAppointmentDataException("Mã dịch vụ không được để trống.");
        }
    }

    public Appointment cancel() {
        if (this.status == AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentDataException("Không thể hủy lịch hẹn đã hoàn thành.");
        }
        if (this.status == AppointmentStatus.CANCELLED) {
            throw new InvalidAppointmentDataException("Lịch hẹn đã được hủy trước đó.");
        }
        return new Appointment(this.id, this.appointmentDate, this.appointmentTime, AppointmentStatus.CANCELLED, this.patientId, this.serviceId);
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getServiceId() {
        return serviceId;
    }
}
