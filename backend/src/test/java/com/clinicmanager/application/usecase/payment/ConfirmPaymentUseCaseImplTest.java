package com.clinicmanager.application.usecase.payment;

import com.clinicmanager.application.dto.payment.InvoiceDto;
import com.clinicmanager.application.mapper.payment.InvoiceMapper;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.application.port.output.payment.PaymentTransactionRepositoryPort;
import com.clinicmanager.domain.exception.payment.InvoiceNotFoundException;
import com.clinicmanager.domain.exception.payment.PaymentTransactionNotFoundException;
import com.clinicmanager.domain.exception.payment.InvalidInvoiceStateException;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmPaymentUseCaseImplTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepositoryPort;

    @Mock
    private PaymentTransactionRepositoryPort paymentTransactionRepositoryPort;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private ConfirmPaymentUseCaseImpl confirmPaymentUseCase;

    private UUID transactionId;
    private UUID invoiceId;
    private PaymentTransaction pendingTransaction;
    private PaymentTransaction successTransaction;
    private Invoice pendingInvoice;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        invoiceId = UUID.randomUUID();

        pendingTransaction = new PaymentTransaction(
                transactionId,
                invoiceId,
                PaymentMethod.CASH,
                BigDecimal.valueOf(150000),
                LocalDateTime.now(),
                null,
                TransactionStatus.PENDING
        );

        successTransaction = new PaymentTransaction(
                transactionId,
                invoiceId,
                PaymentMethod.CASH,
                BigDecimal.valueOf(150000),
                LocalDateTime.now(),
                null,
                TransactionStatus.SUCCESS
        );

        pendingInvoice = new Invoice(
                invoiceId,
                "INV-20260622-0002",
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(100000),
                BigDecimal.ZERO,
                BigDecimal.valueOf(150000),
                InvoiceStatus.PENDING,
                LocalDateTime.now()
        );
    }

    @Test
    void confirmPayment_Success() {
        // Arrange
        when(paymentTransactionRepositoryPort.findById(transactionId)).thenReturn(Optional.of(pendingTransaction));
        when(invoiceRepositoryPort.findById(invoiceId)).thenReturn(Optional.of(pendingInvoice));
        
        Invoice savedInvoice = new Invoice(
                invoiceId,
                "INV-20260622-0002",
                pendingInvoice.getMedicalSlipId(),
                pendingInvoice.getPatientId(),
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(100000),
                BigDecimal.ZERO,
                BigDecimal.valueOf(150000),
                InvoiceStatus.PAID,
                pendingInvoice.getCreatedAt()
        );
        when(invoiceRepositoryPort.save(any(Invoice.class))).thenReturn(savedInvoice);

        InvoiceDto expectedDto = InvoiceDto.builder()
                .id(invoiceId)
                .invoiceNumber("INV-20260622-0002")
                .status(InvoiceStatus.PAID)
                .totalAmount(BigDecimal.valueOf(150000))
                .build();
        when(invoiceMapper.toDto(savedInvoice)).thenReturn(expectedDto);

        // Act
        InvoiceDto result = confirmPaymentUseCase.confirmPayment(transactionId);

        // Assert
        assertNotNull(result);
        assertEquals(InvoiceStatus.PAID, result.getStatus());
        assertEquals(BigDecimal.valueOf(150000), result.getTotalAmount());
        assertEquals(TransactionStatus.SUCCESS, pendingTransaction.getStatus());

        verify(paymentTransactionRepositoryPort).findById(transactionId);
        verify(invoiceRepositoryPort).findById(invoiceId);
        verify(paymentTransactionRepositoryPort).save(pendingTransaction);
        verify(invoiceRepositoryPort).save(any(Invoice.class));
    }

    @Test
    void confirmPayment_ThrowsPaymentTransactionNotFoundException_WhenTransactionDoesNotExist() {
        // Arrange
        when(paymentTransactionRepositoryPort.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentTransactionNotFoundException.class, () -> confirmPaymentUseCase.confirmPayment(transactionId));

        verify(paymentTransactionRepositoryPort).findById(transactionId);
        verifyNoInteractions(invoiceRepositoryPort);
        verify(paymentTransactionRepositoryPort, never()).save(any(PaymentTransaction.class));
    }

    @Test
    void confirmPayment_ThrowsInvoiceNotFoundException_WhenInvoiceDoesNotExist() {
        // Arrange
        when(paymentTransactionRepositoryPort.findById(transactionId)).thenReturn(Optional.of(pendingTransaction));
        when(invoiceRepositoryPort.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvoiceNotFoundException.class, () -> confirmPaymentUseCase.confirmPayment(transactionId));

        verify(paymentTransactionRepositoryPort).findById(transactionId);
        verify(invoiceRepositoryPort).findById(invoiceId);
        verify(paymentTransactionRepositoryPort, never()).save(any(PaymentTransaction.class));
        verify(invoiceRepositoryPort, never()).save(any(Invoice.class));
    }

    @Test
    void confirmPayment_ThrowsInvalidInvoiceStateException_WhenTransactionAlreadySuccess() {
        // Arrange
        when(paymentTransactionRepositoryPort.findById(transactionId)).thenReturn(Optional.of(successTransaction));
        when(invoiceRepositoryPort.findById(invoiceId)).thenReturn(Optional.of(pendingInvoice));

        // Act & Assert
        assertThrows(InvalidInvoiceStateException.class, () -> confirmPaymentUseCase.confirmPayment(transactionId));

        verify(paymentTransactionRepositoryPort).findById(transactionId);
        verify(invoiceRepositoryPort).findById(invoiceId);
        verify(paymentTransactionRepositoryPort, never()).save(any(PaymentTransaction.class));
        verify(invoiceRepositoryPort, never()).save(any(Invoice.class));
    }
}
