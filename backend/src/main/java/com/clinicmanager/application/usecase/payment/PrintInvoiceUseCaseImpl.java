package com.clinicmanager.application.usecase.payment;

import com.clinicmanager.application.dto.examination.ExaminationFeeBreakdownDto;
import com.clinicmanager.application.dto.payment.InvoicePrintDto;
import com.clinicmanager.application.port.input.examination.CalculateExaminationFeeUseCase;
import com.clinicmanager.application.port.input.payment.PrintInvoiceUseCase;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.application.port.output.payment.PaymentTransactionRepositoryPort;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.domain.exception.payment.InvoiceNotFoundException;
import com.clinicmanager.domain.exception.patient.PatientNotFoundException;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
import com.clinicmanager.domain.model.payment.Invoice;
import com.clinicmanager.domain.model.payment.InvoiceStatus;
import com.clinicmanager.domain.model.payment.PaymentTransaction;
import com.clinicmanager.domain.model.payment.TransactionStatus;
import com.clinicmanager.domain.model.patient.Patient;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrintInvoiceUseCaseImpl implements PrintInvoiceUseCase {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final PaymentTransactionRepositoryPort paymentTransactionRepositoryPort;
    private final PatientRepositoryPort patientRepositoryPort;
    private final MedicalSlipRepositoryPort medicalSlipRepositoryPort;
    private final CalculateExaminationFeeUseCase calculateExaminationFeeUseCase;

    @Override
    @Transactional(readOnly = true)
    public InvoicePrintDto getInvoicePrintDetails(UUID invoiceId) {
        // 1. Tìm hóa đơn
        Invoice invoice = invoiceRepositoryPort.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Hóa đơn không tồn tại."));

        // 2. Tìm bệnh nhân
        Patient patient = patientRepositoryPort.findById(invoice.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Bệnh nhân không tồn tại."));

        // 3. Tìm phiếu khám bệnh để lấy ngày khám
        MedicalSlip medicalSlip = medicalSlipRepositoryPort.findById(invoice.getMedicalSlipId())
                .orElseThrow(() -> new MedicalSlipNotFoundException("Phiếu khám không tồn tại."));

        // 4. Lấy chi tiết chi phí
        ExaminationFeeBreakdownDto breakdown = calculateExaminationFeeUseCase.calculate(invoice.getMedicalSlipId());

        // 5. Xác định phương thức thanh toán và thời gian giao dịch thực tế
        List<PaymentTransaction> transactions = paymentTransactionRepositoryPort.findByInvoiceId(invoiceId);
        Optional<PaymentTransaction> successTx = transactions.stream()
                .filter(tx -> tx.getStatus() == TransactionStatus.SUCCESS)
                .findFirst();

        String paymentMethodStr = "Chưa thanh toán";
        LocalDateTime paymentTime = null;

        if (successTx.isPresent()) {
            paymentMethodStr = successTx.get().getPaymentMethod().name();
            paymentTime = successTx.get().getTransactionTime();
        } else if (invoice.getStatus() == InvoiceStatus.PAID) {
            paymentMethodStr = "CASH";
            paymentTime = invoice.getCreatedAt();
        }

        // 6. Dịch tổng tiền ra chữ
        String totalAmountInWords = MoneyToWordsConverter.convert(invoice.getTotalAmount());

        // 7. Xây dựng DTO phản hồi
        return InvoicePrintDto.builder()
                .invoiceId(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .medicalSlipId(invoice.getMedicalSlipId())
                .patientName(patient.getFullName())
                .patientPhone(patient.getPhone())
                .patientAddress(patient.getAddress())
                .examinationDate(medicalSlip.getExaminationDate())
                .examinationService(breakdown.getExaminationService())
                .labTests(breakdown.getLabTests())
                .medicines(breakdown.getMedicines())
                .totalAmount(invoice.getTotalAmount())
                .totalAmountInWords(totalAmountInWords)
                .status(invoice.getStatus().name())
                .paymentMethod(paymentMethodStr)
                .paymentTime(paymentTime)
                .build();
    }
}
