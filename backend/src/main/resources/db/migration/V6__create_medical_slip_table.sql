CREATE TABLE phieu_kham (
    ma_phieu_kham VARCHAR(36) NOT NULL PRIMARY KEY,
    ngay_kham DATE NOT NULL,
    trang_thai VARCHAR(50) NOT NULL,
    ma_benh_nhan VARCHAR(36) NOT NULL,
    CONSTRAINT fk_phieu_kham_benh_nhan FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(ma_benh_nhan)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
