package com.clinicmanager.infrastructure.persistence.appointment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "lich_hen")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEntity {

    @Id
    @Column(name = "ma_lich_hen", length = 36)
    private String id;

    @Column(name = "ngay_hen", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "gio_hen", nullable = false)
    private LocalTime appointmentTime;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String status;

    @Column(name = "ma_benh_nhan", nullable = false, length = 36)
    private String patientId;

    @Column(name = "ma_dich_vu", nullable = false, length = 36)
    private String serviceId;
}
