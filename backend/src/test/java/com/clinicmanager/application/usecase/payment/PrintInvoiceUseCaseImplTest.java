package com.clinicmanager.application.usecase.payment;

import com.clinicmanager.application.dto.examination.ExaminationFeeBreakdownDto;
import com.clinicmanager.application.dto.payment.InvoicePrintDto;
import com.clinicmanager.application.port.input.examination.CalculateExaminationFeeUseCase;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.application.port.output.payment.PaymentTransactionRepositoryPort;
import com.clinicmanager.domain.exception.payment.InvoiceNotFoundException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.patient.Patient;
import com.clinicmanager.domain.model.payment.Invoice;
import com.clinicmanager.domain.model.payment.InvoiceStatus;
import com.clinicmanager.domain.model.payment.PaymentMethod;
import com.clinicmanager.domain.model.payment.PaymentTransaction;
import com.clinicmanager.domain.model.payment.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrintInvoiceUseCaseImplTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepositoryPort;

    @Mock
    private PaymentTransactionRepositoryPort paymentTransactionRepositoryPort;

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    @Mock
    private MedicalSlipRepositoryPort medicalSlipRepositoryPort;

    @Mock
    private CalculateExaminationFeeUseCase calculateExaminationFeeUseCase;

    @InjectMocks
    private PrintInvoiceUseCaseImpl printInvoiceUseCase;

    private UUID invoiceId;
    private UUID medicalSlipId;
    private UUID patientId;

    private Invoice pendingInvoice;
    private Invoice paidInvoice;
    private Patient patient;
    private MedicalSlip medicalSlip;
    private ExaminationFeeBreakdownDto feeBreakdown;

    @BeforeEach
    void setUp() {
        invoiceId = UUID.randomUUID();
        medicalSlipId = UUID.randomUUID();
        patientId = UUID.randomUUID();

        pendingInvoice = new Invoice(
                invoiceId,
                "INV-20260622-0003",
                medicalSlipId,
                patientId,
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(150000),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(210000),
                InvoiceStatus.PENDING,
                LocalDateTime.now()
        );

        paidInvoice = new Invoice(
                invoiceId,
                "INV-20260622-0003",
                medicalSlipId,
                patientId,
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(150000),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(210000),
                InvoiceStatus.PAID,
                LocalDateTime.now()
        );

        patient = new Patient(
                patientId,
                "Nguyễn Văn B",
                LocalDate.of(1985, 5, 20),
                "Nam",
                "0987654321",
                "Hồ Chí Minh",
                "b@gmail.com"
        );

        medicalSlip = new MedicalSlip(
                medicalSlipId,
                LocalDate.now(),
                MedicalSlipStatus.COMPLETED,
                patientId,
                "Ho", 80, 37.0, "120/80", 60.0, 170.0, "Viem phe quan"
        );

        feeBreakdown = ExaminationFeeBreakdownDto.builder()
                .medicalSlipId(medicalSlipId)
                .patientId(patientId)
                .patientName("Nguyễn Văn B")
                .examinationService(ExaminationFeeBreakdownDto.ServiceItem.builder()
                        .serviceName("Khám lâm sàng")
                        .unitPrice(BigDecimal.valueOf(50000))
                        .quantity(1)
                        .subtotal(BigDecimal.valueOf(50000))
                        .build())
                .labTests(Collections.emptyList())
                .medicines(Collections.emptyList())
                .totalAmount(BigDecimal.valueOf(50000))
                .build();
    }

    @Test
    void getInvoicePrintDetails_Success_PendingInvoice() {
        // Arrange
        when(invoiceRepositoryPort.findById(invoiceId)).thenReturn(Optional.of(pendingInvoice));
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(medicalSlip));
        when(calculateExaminationFeeUseCase.calculate(medicalSlipId)).thenReturn(feeBreakdown);
        when(paymentTransactionRepositoryPort.findByInvoiceId(invoiceId)).thenReturn(Collections.emptyList());

        // Act
        InvoicePrintDto result = printInvoiceUseCase.getInvoicePrintDetails(invoiceId);

        // Assert
        assertNotNull(result);
        assertEquals(invoiceId, result.getInvoiceId());
        assertEquals("INV-20260622-0003", result.getInvoiceNumber());
        assertEquals("Nguyễn Văn B", result.getPatientName());
        assertEquals("Chưa thanh toán", result.getPaymentMethod());
        assertNull(result.getPaymentTime());
        assertEquals("Hai trăm mười ngàn đồng", result.getTotalAmountInWords());
        assertEquals(BigDecimal.valueOf(210000), result.getTotalAmount());
    }

    @Test
    void getInvoicePrintDetails_Success_PaidInvoice() {
        // Arrange
        when(invoiceRepositoryPort.findById(invoiceId)).thenReturn(Optional.of(paidInvoice));
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(medicalSlip));
        when(calculateExaminationFeeUseCase.calculate(medicalSlipId)).thenReturn(feeBreakdown);

        PaymentTransaction successTx = new PaymentTransaction(
                UUID.randomUUID(),
                invoiceId,
                PaymentMethod.BANK_TRANSFER,
                BigDecimal.valueOf(210000),
                LocalDateTime.of(2026, 6, 22, 9, 0),
                "TX8888",
                TransactionStatus.SUCCESS
        );
        when(paymentTransactionRepositoryPort.findByInvoiceId(invoiceId)).thenReturn(List.of(successTx));

        // Act
        InvoicePrintDto result = printInvoiceUseCase.getInvoicePrintDetails(invoiceId);

        // Assert
        assertNotNull(result);
        assertEquals(invoiceId, result.getInvoiceId());
        assertEquals("BANK_TRANSFER", result.getPaymentMethod());
        assertEquals(LocalDateTime.of(2026, 6, 22, 9, 0), result.getPaymentTime());
        assertEquals("Hai trăm mười ngàn đồng", result.getTotalAmountInWords());
    }

    @Test
    void getInvoicePrintDetails_ThrowsInvoiceNotFoundException_WhenInvoiceDoesNotExist() {
        // Arrange
        when(invoiceRepositoryPort.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvoiceNotFoundException.class, () -> printInvoiceUseCase.getInvoicePrintDetails(invoiceId));
    }

    @Test
    void testMoneyToWordsConverter() {
        assertEquals("Hai trăm mười ngàn đồng", MoneyToWordsConverter.convert(BigDecimal.valueOf(210000)));
        assertEquals("Một triệu năm trăm năm mươi ngàn đồng", MoneyToWordsConverter.convert(BigDecimal.valueOf(1550000)));
        assertEquals("Không đồng", MoneyToWordsConverter.convert(BigDecimal.valueOf(0)));
        assertEquals("Một ngàn đồng", MoneyToWordsConverter.convert(BigDecimal.valueOf(1000)));
    }
}
