package com.clinicmanager.infrastructure.persistence.admission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "phieu_kham")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalSlipEntity {

    @Id
    @Column(name = "ma_phieu_kham", length = 36)
    private String id;

    @Column(name = "ngay_kham", nullable = false)
    private LocalDate examinationDate;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String status;

    @Column(name = "ma_benh_nhan", nullable = false, length = 36)
    private String patientId;

    @Column(name = "trieu_chung", length = 1000)
    private String symptoms;

    @Column(name = "mach")
    private Integer pulse;

    @Column(name = "nhiet_do", precision = 4, scale = 1)
    private Double temperature;

    @Column(name = "huyet_ap", length = 20)
    private String bloodPressure;

    @Column(name = "can_nang", precision = 5, scale = 2)
    private Double weight;

    @Column(name = "chieu_cao", precision = 5, scale = 2)
    private Double height;
}
