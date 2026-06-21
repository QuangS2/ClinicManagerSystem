CREATE TABLE lich_hen (
    ma_lich_hen VARCHAR(36) NOT NULL PRIMARY KEY,
    ngay_hen DATE NOT NULL,
    gio_hen TIME NOT NULL,
    trang_thai VARCHAR(50) NOT NULL,
    ma_benh_nhan VARCHAR(36) NOT NULL,
    ma_dich_vu VARCHAR(36) NOT NULL,
    CONSTRAINT fk_lich_hen_benh_nhan FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(ma_benh_nhan),
    CONSTRAINT fk_lich_hen_dich_vu FOREIGN KEY (ma_dich_vu) REFERENCES dich_vu(ma_dich_vu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
