package com.clinicmanager.domain.model.payment;

public enum TransactionStatus {
    PENDING, // Chờ xử lý / chờ chuyển khoản
    SUCCESS, // Thành công
    FAILED   // Thất bại / Lỗi
}
