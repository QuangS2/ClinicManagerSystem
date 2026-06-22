-- Seed users (tai_khoan)
-- admin: admin123 -> $2a$10$ZD/3Cd/7N0SL0KQHQazdM./tlKN7ZK.H2Gy.IulstOyMcaLjBt2Me
-- doctor: doctor123 -> $2a$10$FEdkqf4QdwXgU6unqXWQ7eVyCfrOScZvMFMAfaXQskvPrHwcCfNzO
-- receptionist: receptionist123 -> $2a$10$iVhe94DFfU5kMWxtaTC/5eajVlnRIJ/oHI2e/cROoaW8pTPrU8n0y
-- cashier: cashier123 -> $2a$10$LYWsmWUq6PjE8p4qJ2S7buNqSEJmz3kJsUSSLHZaBNWzn4R2Aj2X6
-- tech: tech123 -> $2a$10$hc5uZOWWbEbM0ZKckjkSGOee8UqJqq71gBhxsGWCULWc/i7IQBD0S

INSERT INTO tai_khoan (ma_tai_khoan, ten_dang_nhap, mat_khau, vai_tro, trang_thai) VALUES
('a0e0b9b0-1234-4321-8765-000000000001', 'admin', '$2a$10$ZD/3Cd/7N0SL0KQHQazdM./tlKN7ZK.H2Gy.IulstOyMcaLjBt2Me', 'QUAN_LY', 'ACTIVE'),
('a0e0b9b0-1234-4321-8765-000000000002', 'doctor', '$2a$10$FEdkqf4QdwXgU6unqXWQ7eVyCfrOScZvMFMAfaXQskvPrHwcCfNzO', 'BAC_SI', 'ACTIVE'),
('a0e0b9b0-1234-4321-8765-000000000003', 'receptionist', '$2a$10$iVhe94DFfU5kMWxtaTC/5eajVlnRIJ/oHI2e/cROoaW8pTPrU8n0y', 'LE_TAN', 'ACTIVE'),
('a0e0b9b0-1234-4321-8765-000000000004', 'cashier', '$2a$10$LYWsmWUq6PjE8p4qJ2S7buNqSEJmz3kJsUSSLHZaBNWzn4R2Aj2X6', 'THU_NGAN', 'ACTIVE'),
('a0e0b9b0-1234-4321-8765-000000000005', 'tech', '$2a$10$hc5uZOWWbEbM0ZKckjkSGOee8UqJqq71gBhxsGWCULWc/i7IQBD0S', 'KTV', 'ACTIVE');

-- Seed services (dich_vu)
INSERT INTO dich_vu (ma_dich_vu, ten_dich_vu, don_gia, mo_ta) VALUES
('b0e0b9b0-1234-4321-8765-000000000001', 'Khám lâm sàng', 150000.00, 'Khám tổng quát lâm sàng ban đầu'),
('b0e0b9b0-1234-4321-8765-000000000002', 'Xét nghiệm công thức máu', 200000.00, 'Xét nghiệm chỉ số máu cơ bản'),
('b0e0b9b0-1234-4321-8765-000000000003', 'Siêu âm ổ bụng tổng quát', 250000.00, 'Siêu âm kiểm tra các cơ quan vùng bụng'),
('b0e0b9b0-1234-4321-8765-000000000004', 'Chụp X-quang phổi', 180000.00, 'Chụp X-quang lồng ngực kiểm tra phổi');

-- Seed medicines (thuoc)
INSERT INTO thuoc (ma_thuoc, ten_thuoc, don_vi_tinh, don_gia, so_luong_ton) VALUES
('c0e0b9b0-1234-4321-8765-000000000001', 'Paracetamol 500mg', 'Viên', 1000.00, 1000),
('c0e0b9b0-1234-4321-8765-000000000002', 'Amoxicillin 500mg', 'Viên', 2500.00, 500),
('c0e0b9b0-1234-4321-8765-000000000003', 'Ibuprofen 400mg', 'Viên', 1500.00, 800),
('c0e0b9b0-1234-4321-8765-000000000004', 'Decolgen', 'Vỉ', 15000.00, 200);
