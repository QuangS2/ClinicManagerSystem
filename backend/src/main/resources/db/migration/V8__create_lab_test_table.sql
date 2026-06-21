CREATE TABLE xet_nghiem (
    ma_xet_nghiem VARCHAR(36) NOT NULL PRIMARY KEY,
    loai_xet_nghiem VARCHAR(255) NOT NULL,
    ket_qua VARCHAR(1000),
    ngay_xet_nghiem DATE,
    ma_phieu_kham VARCHAR(36) NOT NULL,
    CONSTRAINT fk_xet_nghiem_phieu_kham FOREIGN KEY (ma_phieu_kham) REFERENCES phieu_kham(ma_phieu_kham)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
