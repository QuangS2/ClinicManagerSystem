package com.clinicmanager.application.usecase.payment;

import com.clinicmanager.application.dto.payment.InvoiceDto;
import com.clinicmanager.application.mapper.payment.InvoiceMapper;
import com.clinicmanager.application.port.input.payment.ConfirmPaymentUseCase;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.application.port.output.payment.PaymentTransactionRepositoryPort;
import com.clinicmanager.domain.exception.payment.InvoiceNotFoundException;
import com.clinicmanager.domain.exception.payment.PaymentTransactionNotFoundException;
import com.clinicmanager.domain.exception.payment.InvalidInvoiceStateException;
import com.clinicmanager.domain.model.payment.Invoice;
import com.clinicmanager.domain.model.payment.InvoiceStatus;
import com.clinicmanager.domain.model.payment.PaymentTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmPaymentUseCaseImpl implements ConfirmPaymentUseCase {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final PaymentTransactionRepositoryPort paymentTransactionRepositoryPort;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional
    public InvoiceDto confirmPayment(UUID transactionId) {
        // 1. Tìm giao dịch thanh toán
        PaymentTransaction transaction = paymentTransactionRepositoryPort.findById(transactionId)
                .orElseThrow(() -> new PaymentTransactionNotFoundException("Giao dịch thanh toán không tồn tại."));

        // 2. Tìm hóa đơn tương ứng
        Invoice invoice = invoiceRepositoryPort.findById(transaction.getInvoiceId())
                .orElseThrow(() -> new InvoiceNotFoundException("Hóa đơn liên kết với giao dịch không tồn tại."));

        // 3. Thực thi nghiệp vụ cập nhật giao dịch
        transaction.complete();
        paymentTransactionRepositoryPort.save(transaction);

        // 4. Cập nhật trạng thái hóa đơn sang PAID
        Invoice updatedInvoice = new Invoice(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getMedicalSlipId(),
                invoice.getPatientId(),
                invoice.getClinicalExamAmount(),
                invoice.getLabTestAmount(),
                invoice.getMedicineAmount(),
                invoice.getTotalAmount(),
                InvoiceStatus.PAID,
                invoice.getCreatedAt()
        );

        Invoice savedInvoice = invoiceRepositoryPort.save(updatedInvoice);

        return invoiceMapper.toDto(savedInvoice);
    }
}
