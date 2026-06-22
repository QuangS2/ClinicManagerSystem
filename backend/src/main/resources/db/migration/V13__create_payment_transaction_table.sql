CREATE TABLE giao_dich_thanh_toan (
    ma_giao_dich VARCHAR(36) NOT NULL PRIMARY KEY,
    ma_hoa_don VARCHAR(36) NOT NULL,
    phuong_thuc_thanh_toan VARCHAR(50) NOT NULL,
    so_tien DECIMAL(15,2) NOT NULL,
    ngay_giao_dich TIMESTAMP NOT NULL,
    ma_chuyen_khoan VARCHAR(100),
    trang_thai VARCHAR(50) NOT NULL,
    CONSTRAINT fk_giao_dich_hoa_don FOREIGN KEY (ma_hoa_don) REFERENCES hoa_don(ma_hoa_don)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
