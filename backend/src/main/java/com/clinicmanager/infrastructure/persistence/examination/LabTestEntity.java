package com.clinicmanager.infrastructure.persistence.examination;

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
@Table(name = "xet_nghiem")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabTestEntity {

    @Id
    @Column(name = "ma_xet_nghiem", length = 36)
    private String id;

    @Column(name = "loai_xet_nghiem", nullable = false)
    private String testType;

    @Column(name = "ket_qua", length = 1000)
    private String result;

    @Column(name = "ngay_xet_nghiem")
    private LocalDate testDate;

    @Column(name = "ma_phieu_kham", nullable = false, length = 36)
    private String medicalSlipId;
}
