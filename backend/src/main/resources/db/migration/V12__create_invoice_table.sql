CREATE TABLE hoa_don (
    ma_hoa_don VARCHAR(36) NOT NULL PRIMARY KEY,
    so_hoa_don VARCHAR(50) NOT NULL UNIQUE,
    ma_phieu_kham VARCHAR(36) NOT NULL UNIQUE,
    ma_benh_nhan VARCHAR(36) NOT NULL,
    tien_kham DECIMAL(15,2) NOT NULL,
    tien_xet_nghiem DECIMAL(15,2) NOT NULL,
    tien_thuoc DECIMAL(15,2) NOT NULL,
    tong_tien DECIMAL(15,2) NOT NULL,
    trang_thai VARCHAR(50) NOT NULL,
    ngay_tao TIMESTAMP NOT NULL,
    CONSTRAINT fk_hoa_don_phieu_kham FOREIGN KEY (ma_phieu_kham) REFERENCES phieu_kham(ma_phieu_kham),
    CONSTRAINT fk_hoa_don_benh_nhan FOREIGN KEY (ma_benh_nhan) REFERENCES benh_nhan(ma_benh_nhan)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
