package com.clinicmanager.domain.model.payment;

import com.clinicmanager.domain.exception.payment.InvalidInvoiceStateException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Invoice {
    private final UUID id;
    private final String invoiceNumber;
    private final UUID medicalSlipId;
    private final UUID patientId;
    private final BigDecimal clinicalExamAmount;
    private final BigDecimal labTestAmount;
    private final BigDecimal medicineAmount;
    private final BigDecimal totalAmount;
    private final InvoiceStatus status;
    private final LocalDateTime createdAt;

    public Invoice(UUID id, String invoiceNumber, UUID medicalSlipId, UUID patientId,
                   BigDecimal clinicalExamAmount, BigDecimal labTestAmount, BigDecimal medicineAmount,
                   BigDecimal totalAmount, InvoiceStatus status, LocalDateTime createdAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.invoiceNumber = invoiceNumber;
        this.medicalSlipId = medicalSlipId;
        this.patientId = patientId;
        this.clinicalExamAmount = clinicalExamAmount != null ? clinicalExamAmount : BigDecimal.ZERO;
        this.labTestAmount = labTestAmount != null ? labTestAmount : BigDecimal.ZERO;
        this.medicineAmount = medicineAmount != null ? medicineAmount : BigDecimal.ZERO;
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        this.status = status != null ? status : InvoiceStatus.PENDING;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        validate();
    }

    private void validate() {
        if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
            throw new InvalidInvoiceStateException("Số hóa đơn không được để trống.");
        }
        if (medicalSlipId == null) {
            throw new InvalidInvoiceStateException("Mã phiếu khám không được để trống.");
        }
        if (patientId == null) {
            throw new InvalidInvoiceStateException("Mã bệnh nhân không được để trống.");
        }
        if (clinicalExamAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInvoiceStateException("Tiền khám lâm sàng không được âm.");
        }
        if (labTestAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInvoiceStateException("Tiền xét nghiệm không được âm.");
        }
        if (medicineAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInvoiceStateException("Tiền thuốc không được âm.");
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInvoiceStateException("Tổng tiền không được âm.");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public UUID getMedicalSlipId() {
        return medicalSlipId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public BigDecimal getClinicalExamAmount() {
        return clinicalExamAmount;
    }

    public BigDecimal getLabTestAmount() {
        return labTestAmount;
    }

    public BigDecimal getMedicineAmount() {
        return medicineAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
