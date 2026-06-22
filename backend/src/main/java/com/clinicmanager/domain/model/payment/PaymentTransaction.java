package com.clinicmanager.domain.model.payment;

import com.clinicmanager.domain.exception.payment.InvalidInvoiceStateException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentTransaction {
    private final UUID id;
    private final UUID invoiceId;
    private final PaymentMethod paymentMethod;
    private final BigDecimal amount;
    private final LocalDateTime transactionTime;
    private final String transferCode;
    private TransactionStatus status;

    public PaymentTransaction(UUID id, UUID invoiceId, PaymentMethod paymentMethod, BigDecimal amount,
                              LocalDateTime transactionTime, String transferCode, TransactionStatus status) {
        this.id = id != null ? id : UUID.randomUUID();
        this.invoiceId = invoiceId;
        this.paymentMethod = paymentMethod;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.transactionTime = transactionTime != null ? transactionTime : LocalDateTime.now();
        this.transferCode = transferCode;
        this.status = status != null ? status : TransactionStatus.PENDING;
        validate();
    }

    private void validate() {
        if (invoiceId == null) {
            throw new InvalidInvoiceStateException("Mã hóa đơn không được để trống.");
        }
        if (paymentMethod == null) {
            throw new InvalidInvoiceStateException("Phương thức thanh toán không được để trống.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInvoiceStateException("Số tiền thanh toán phải lớn hơn 0.");
        }
        if (status == null) {
            throw new InvalidInvoiceStateException("Trạng thái giao dịch không được để trống.");
        }
    }

    public void complete() {
        if (this.status != TransactionStatus.PENDING) {
            throw new InvalidInvoiceStateException("Chỉ có thể hoàn thành giao dịch đang ở trạng thái PENDING.");
        }
        this.status = TransactionStatus.SUCCESS;
    }

    public void fail() {
        if (this.status != TransactionStatus.PENDING) {
            throw new InvalidInvoiceStateException("Chỉ có thể đánh dấu thất bại cho giao dịch đang ở trạng thái PENDING.");
        }
        this.status = TransactionStatus.FAILED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public String getTransferCode() {
        return transferCode;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}
