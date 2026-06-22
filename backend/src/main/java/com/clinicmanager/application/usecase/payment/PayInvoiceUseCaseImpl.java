package com.clinicmanager.application.usecase.payment;

import com.clinicmanager.application.dto.payment.PayInvoiceRequest;
import com.clinicmanager.application.dto.payment.PaymentTransactionDto;
import com.clinicmanager.application.mapper.payment.PaymentTransactionMapper;
import com.clinicmanager.application.port.input.payment.PayInvoiceUseCase;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.application.port.output.payment.PaymentTransactionRepositoryPort;
import com.clinicmanager.domain.exception.payment.InvoiceNotFoundException;
import com.clinicmanager.domain.exception.payment.InvalidInvoiceStateException;
import com.clinicmanager.domain.model.payment.Invoice;
import com.clinicmanager.domain.model.payment.InvoiceStatus;
import com.clinicmanager.domain.model.payment.PaymentTransaction;
import com.clinicmanager.domain.model.payment.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayInvoiceUseCaseImpl implements PayInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final PaymentTransactionRepositoryPort paymentTransactionRepositoryPort;
    private final PaymentTransactionMapper paymentTransactionMapper;

    @Override
    @Transactional
    public PaymentTransactionDto payInvoice(PayInvoiceRequest request) {
        UUID invoiceId = request.getInvoiceId();

        // 1. Kiểm tra hóa đơn tồn tại
        Invoice invoice = invoiceRepositoryPort.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Hóa đơn không tồn tại."));

        // 2. Kiểm tra hóa đơn phải ở trạng thái PENDING
        if (invoice.getStatus() != InvoiceStatus.PENDING) {
            throw new InvalidInvoiceStateException("Chỉ có thể thanh toán cho hóa đơn ở trạng thái PENDING.");
        }

        // 3. Khởi tạo giao dịch thanh toán
        PaymentTransaction transaction = new PaymentTransaction(
                null,
                invoiceId,
                request.getPaymentMethod(),
                invoice.getTotalAmount(),
                LocalDateTime.now(),
                request.getTransferCode(),
                TransactionStatus.PENDING
        );

        PaymentTransaction savedTransaction = paymentTransactionRepositoryPort.save(transaction);

        return paymentTransactionMapper.toDto(savedTransaction);
    }
}
