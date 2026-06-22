package com.clinicmanager.infrastructure.persistence.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

    @Id
    @Column(name = "ma_hoa_don", length = 36)
    private String id;

    @Column(name = "so_hoa_don", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @Column(name = "ma_phieu_kham", nullable = false, unique = true, length = 36)
    private String medicalSlipId;

    @Column(name = "ma_benh_nhan", nullable = false, length = 36)
    private String patientId;

    @Column(name = "tien_kham", nullable = false, precision = 15, scale = 2)
    private BigDecimal clinicalExamAmount;

    @Column(name = "tien_xet_nghiem", nullable = false, precision = 15, scale = 2)
    private BigDecimal labTestAmount;

    @Column(name = "tien_thuoc", nullable = false, precision = 15, scale = 2)
    private BigDecimal medicineAmount;

    @Column(name = "tong_tien", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String status;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime createdAt;
}
