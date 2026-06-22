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
@Table(name = "giao_dich_thanh_toan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionEntity {

    @Id
    @Column(name = "ma_giao_dich", length = 36)
    private String id;

    @Column(name = "ma_hoa_don", nullable = false, length = 36)
    private String invoiceId;

    @Column(name = "phuong_thuc_thanh_toan", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "so_tien", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "ngay_giao_dich", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "ma_chuyen_khoan", length = 100)
    private String transferCode;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String status;
}
