package com.clinicmanager.application.usecase.payment;

import com.clinicmanager.application.dto.examination.ExaminationFeeBreakdownDto;
import com.clinicmanager.application.dto.payment.CreateInvoiceRequest;
import com.clinicmanager.application.dto.payment.InvoiceDto;
import com.clinicmanager.application.mapper.payment.InvoiceMapper;
import com.clinicmanager.application.port.input.examination.CalculateExaminationFeeUseCase;
import com.clinicmanager.application.port.input.payment.CreateInvoiceUseCase;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.domain.exception.payment.InvoiceAlreadyExistsException;
import com.clinicmanager.domain.model.payment.Invoice;
import com.clinicmanager.domain.model.payment.InvoiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateInvoiceUseCaseImpl implements CreateInvoiceUseCase {

    private final CalculateExaminationFeeUseCase calculateExaminationFeeUseCase;
    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        UUID medicalSlipId = request.getMedicalSlipId();

        // 1. Kiểm tra hóa đơn đã tồn tại cho phiếu khám này chưa
        Optional<Invoice> existingInvoice = invoiceRepositoryPort.findByMedicalSlipId(medicalSlipId);
        if (existingInvoice.isPresent()) {
            throw new InvoiceAlreadyExistsException("Hóa đơn cho phiếu khám này đã tồn tại.");
        }

        // 2. Tính toán chi phí (tự động kiểm tra tính hợp lệ của phiếu khám và trạng thái COMPLETED)
        ExaminationFeeBreakdownDto breakdown = calculateExaminationFeeUseCase.calculate(medicalSlipId);

        // 3. Tạo mã hóa đơn duy nhất
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomVal = (int) (Math.random() * 900) + 100;
        String invoiceNumber = "INV-" + dateStr + "-" + randomVal;

        // 4. Tính các khoản chi phí chi tiết
        BigDecimal examAmount = breakdown.getExaminationService() != null 
                ? breakdown.getExaminationService().getSubtotal() 
                : BigDecimal.ZERO;

        BigDecimal labTestAmount = breakdown.getLabTests() != null 
                ? breakdown.getLabTests().stream()
                    .map(ExaminationFeeBreakdownDto.ServiceItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add) 
                : BigDecimal.ZERO;

        BigDecimal medicineAmount = breakdown.getMedicines() != null 
                ? breakdown.getMedicines().stream()
                    .map(ExaminationFeeBreakdownDto.MedicineItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add) 
                : BigDecimal.ZERO;

        BigDecimal totalAmount = breakdown.getTotalAmount() != null 
                ? breakdown.getTotalAmount() 
                : BigDecimal.ZERO;

        // 5. Tạo và lưu hóa đơn mới
        Invoice invoice = new Invoice(
                null,
                invoiceNumber,
                medicalSlipId,
                breakdown.getPatientId(),
                examAmount,
                labTestAmount,
                medicineAmount,
                totalAmount,
                InvoiceStatus.PENDING,
                LocalDateTime.now()
        );

        Invoice savedInvoice = invoiceRepositoryPort.save(invoice);

        return invoiceMapper.toDto(savedInvoice);
    }
}
