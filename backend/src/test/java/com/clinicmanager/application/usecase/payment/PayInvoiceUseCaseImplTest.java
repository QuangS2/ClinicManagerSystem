package com.clinicmanager.application.usecase.payment;

import com.clinicmanager.application.dto.payment.PayInvoiceRequest;
import com.clinicmanager.application.dto.payment.PaymentTransactionDto;
import com.clinicmanager.application.mapper.payment.PaymentTransactionMapper;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.application.port.output.payment.PaymentTransactionRepositoryPort;
import com.clinicmanager.domain.exception.payment.InvoiceNotFoundException;
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
class PayInvoiceUseCaseImplTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepositoryPort;

    @Mock
    private PaymentTransactionRepositoryPort paymentTransactionRepositoryPort;

    @Mock
    private PaymentTransactionMapper paymentTransactionMapper;

    @InjectMocks
    private PayInvoiceUseCaseImpl payInvoiceUseCase;

    private UUID invoiceId;
    private PayInvoiceRequest request;
    private Invoice pendingInvoice;
    private Invoice paidInvoice;

    @BeforeEach
    void setUp() {
        invoiceId = UUID.randomUUID();

        request = PayInvoiceRequest.builder()
                .invoiceId(invoiceId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .transferCode("TX123456")
                .build();

        pendingInvoice = new Invoice(
                invoiceId,
                "INV-20260622-0001",
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(150000),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(210000),
                InvoiceStatus.PENDING,
                LocalDateTime.now()
        );

        paidInvoice = new Invoice(
                invoiceId,
                "INV-20260622-0001",
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(150000),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(210000),
                InvoiceStatus.PAID,
                LocalDateTime.now()
        );
    }

    @Test
    void payInvoice_Success() {
        // Arrange
        when(invoiceRepositoryPort.findById(invoiceId)).thenReturn(Optional.of(pendingInvoice));

        PaymentTransaction savedTransaction = new PaymentTransaction(
                UUID.randomUUID(),
                invoiceId,
                PaymentMethod.BANK_TRANSFER,
                BigDecimal.valueOf(210000),
                LocalDateTime.now(),
                "TX123456",
                TransactionStatus.PENDING
        );
        when(paymentTransactionRepositoryPort.save(any(PaymentTransaction.class))).thenReturn(savedTransaction);

        PaymentTransactionDto expectedDto = PaymentTransactionDto.builder()
                .id(savedTransaction.getId())
                .invoiceId(invoiceId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(BigDecimal.valueOf(210000))
                .transactionTime(savedTransaction.getTransactionTime())
                .transferCode("TX123456")
                .status(TransactionStatus.PENDING)
                .build();
        when(paymentTransactionMapper.toDto(savedTransaction)).thenReturn(expectedDto);

        // Act
        PaymentTransactionDto result = payInvoiceUseCase.payInvoice(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(invoiceId, result.getInvoiceId());
        assertEquals(PaymentMethod.BANK_TRANSFER, result.getPaymentMethod());
        assertEquals(BigDecimal.valueOf(210000), result.getAmount());
        assertEquals("TX123456", result.getTransferCode());
        assertEquals(TransactionStatus.PENDING, result.getStatus());

        verify(invoiceRepositoryPort).findById(invoiceId);
        verify(paymentTransactionRepositoryPort).save(any(PaymentTransaction.class));
    }

    @Test
    void payInvoice_ThrowsInvoiceNotFoundException_WhenInvoiceDoesNotExist() {
        // Arrange
        when(invoiceRepositoryPort.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvoiceNotFoundException.class, () -> payInvoiceUseCase.payInvoice(request));

        verify(invoiceRepositoryPort).findById(invoiceId);
        verifyNoInteractions(paymentTransactionRepositoryPort);
    }

    @Test
    void payInvoice_ThrowsInvalidInvoiceStateException_WhenInvoiceIsNotPending() {
        // Arrange
        when(invoiceRepositoryPort.findById(invoiceId)).thenReturn(Optional.of(paidInvoice));

        // Act & Assert
        assertThrows(InvalidInvoiceStateException.class, () -> payInvoiceUseCase.payInvoice(request));

        verify(invoiceRepositoryPort).findById(invoiceId);
        verifyNoInteractions(paymentTransactionRepositoryPort);
    }
}
