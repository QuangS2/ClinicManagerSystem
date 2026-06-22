package com.clinicmanager.application.usecase.payment;

import com.clinicmanager.application.dto.examination.ExaminationFeeBreakdownDto;
import com.clinicmanager.application.dto.payment.CreateInvoiceRequest;
import com.clinicmanager.application.dto.payment.InvoiceDto;
import com.clinicmanager.application.mapper.payment.InvoiceMapper;
import com.clinicmanager.application.port.input.examination.CalculateExaminationFeeUseCase;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
import com.clinicmanager.domain.exception.payment.InvoiceAlreadyExistsException;
import com.clinicmanager.domain.model.payment.Invoice;
import com.clinicmanager.domain.model.payment.InvoiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateInvoiceUseCaseImplTest {

    @Mock
    private CalculateExaminationFeeUseCase calculateExaminationFeeUseCase;

    @Mock
    private InvoiceRepositoryPort invoiceRepositoryPort;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private CreateInvoiceUseCaseImpl createInvoiceUseCase;

    private UUID medicalSlipId;
    private UUID patientId;
    private CreateInvoiceRequest request;
    private ExaminationFeeBreakdownDto feeBreakdown;

    @BeforeEach
    void setUp() {
        medicalSlipId = UUID.randomUUID();
        patientId = UUID.randomUUID();

        request = CreateInvoiceRequest.builder()
                .medicalSlipId(medicalSlipId)
                .build();

        ExaminationFeeBreakdownDto.ServiceItem examService = ExaminationFeeBreakdownDto.ServiceItem.builder()
                .serviceName("Khám lâm sàng")
                .unitPrice(BigDecimal.valueOf(50000))
                .quantity(1)
                .subtotal(BigDecimal.valueOf(50000))
                .build();

        ExaminationFeeBreakdownDto.ServiceItem labTest = ExaminationFeeBreakdownDto.ServiceItem.builder()
                .serviceName("Xét nghiệm máu")
                .unitPrice(BigDecimal.valueOf(150000))
                .quantity(1)
                .subtotal(BigDecimal.valueOf(150000))
                .build();

        ExaminationFeeBreakdownDto.MedicineItem medicine = ExaminationFeeBreakdownDto.MedicineItem.builder()
                .medicineName("Paracetamol")
                .unitPrice(BigDecimal.valueOf(1000))
                .quantity(10)
                .subtotal(BigDecimal.valueOf(10000))
                .build();

        feeBreakdown = ExaminationFeeBreakdownDto.builder()
                .medicalSlipId(medicalSlipId)
                .patientId(patientId)
                .patientName("Nguyễn Văn A")
                .examinationService(examService)
                .labTests(List.of(labTest))
                .medicines(List.of(medicine))
                .totalAmount(BigDecimal.valueOf(210000)) // 50000 + 150000 + 10000
                .build();
    }

    @Test
    void createInvoice_Success() {
        // Arrange
        when(invoiceRepositoryPort.findByMedicalSlipId(medicalSlipId)).thenReturn(Optional.empty());
        when(calculateExaminationFeeUseCase.calculate(medicalSlipId)).thenReturn(feeBreakdown);
        
        Invoice savedInvoice = new Invoice(
                UUID.randomUUID(),
                "INV-20260622-0001",
                medicalSlipId,
                patientId,
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(150000),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(210000),
                InvoiceStatus.PENDING,
                LocalDateTime.now()
        );
        when(invoiceRepositoryPort.save(any(Invoice.class))).thenReturn(savedInvoice);

        InvoiceDto expectedDto = InvoiceDto.builder()
                .id(savedInvoice.getId())
                .invoiceNumber(savedInvoice.getInvoiceNumber())
                .medicalSlipId(medicalSlipId)
                .patientId(patientId)
                .clinicalExamAmount(BigDecimal.valueOf(50000))
                .labTestAmount(BigDecimal.valueOf(150000))
                .medicineAmount(BigDecimal.valueOf(10000))
                .totalAmount(BigDecimal.valueOf(210000))
                .status(InvoiceStatus.PENDING)
                .createdAt(savedInvoice.getCreatedAt())
                .build();
        when(invoiceMapper.toDto(savedInvoice)).thenReturn(expectedDto);

        // Act
        InvoiceDto result = createInvoiceUseCase.createInvoice(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getInvoiceNumber(), result.getInvoiceNumber());
        assertEquals(medicalSlipId, result.getMedicalSlipId());
        assertEquals(patientId, result.getPatientId());
        assertEquals(BigDecimal.valueOf(50000), result.getClinicalExamAmount());
        assertEquals(BigDecimal.valueOf(150000), result.getLabTestAmount());
        assertEquals(BigDecimal.valueOf(10000), result.getMedicineAmount());
        assertEquals(BigDecimal.valueOf(210000), result.getTotalAmount());
        assertEquals(InvoiceStatus.PENDING, result.getStatus());

        verify(invoiceRepositoryPort).findByMedicalSlipId(medicalSlipId);
        verify(calculateExaminationFeeUseCase).calculate(medicalSlipId);
        verify(invoiceRepositoryPort).save(any(Invoice.class));
    }

    @Test
    void createInvoice_ThrowsInvoiceAlreadyExistsException_WhenInvoiceExists() {
        // Arrange
        Invoice existingInvoice = mock(Invoice.class);
        when(invoiceRepositoryPort.findByMedicalSlipId(medicalSlipId)).thenReturn(Optional.of(existingInvoice));

        // Act & Assert
        assertThrows(InvoiceAlreadyExistsException.class, () -> createInvoiceUseCase.createInvoice(request));

        verify(invoiceRepositoryPort).findByMedicalSlipId(medicalSlipId);
        verifyNoInteractions(calculateExaminationFeeUseCase);
        verify(invoiceRepositoryPort, never()).save(any(Invoice.class));
    }

    @Test
    void createInvoice_PropagatesException_WhenFeeCalculationFails() {
        // Arrange
        when(invoiceRepositoryPort.findByMedicalSlipId(medicalSlipId)).thenReturn(Optional.empty());
        when(calculateExaminationFeeUseCase.calculate(medicalSlipId))
                .thenThrow(new MedicalSlipNotFoundException("Phiếu khám không tồn tại."));

        // Act & Assert
        assertThrows(MedicalSlipNotFoundException.class, () -> createInvoiceUseCase.createInvoice(request));

        verify(invoiceRepositoryPort).findByMedicalSlipId(medicalSlipId);
        verify(calculateExaminationFeeUseCase).calculate(medicalSlipId);
        verify(invoiceRepositoryPort, never()).save(any(Invoice.class));
    }
}
