package com.clinicmanager.presentation.controller.payment;

import com.clinicmanager.application.dto.payment.CreateInvoiceRequest;
import com.clinicmanager.application.dto.payment.InvoiceDto;
import com.clinicmanager.application.port.input.payment.CreateInvoiceUseCase;
import com.clinicmanager.presentation.request.payment.SubmitCreateInvoiceRequest;
import com.clinicmanager.presentation.response.ApiResponse;
import com.clinicmanager.presentation.response.payment.InvoiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final CreateInvoiceUseCase createInvoiceUseCase;
    private final com.clinicmanager.application.port.input.payment.PayInvoiceUseCase payInvoiceUseCase;
    private final com.clinicmanager.application.port.input.payment.ConfirmPaymentUseCase confirmPaymentUseCase;
    private final com.clinicmanager.application.port.input.payment.PrintInvoiceUseCase printInvoiceUseCase;

    @PostMapping
    public ApiResponse<InvoiceResponse> create(@Valid @RequestBody SubmitCreateInvoiceRequest request) {
        CreateInvoiceRequest appRequest = CreateInvoiceRequest.builder()
                .medicalSlipId(request.getMedicalSlipId())
                .build();

        InvoiceDto dto = createInvoiceUseCase.createInvoice(appRequest);

        InvoiceResponse response = mapToResponse(dto);

        return ApiResponse.success(response, "Lập hóa đơn thành công.");
    }

    @PostMapping("/{invoiceId}/pay")
    public ApiResponse<com.clinicmanager.presentation.response.payment.PaymentTransactionResponse> pay(
            @PathVariable java.util.UUID invoiceId,
            @Valid @RequestBody com.clinicmanager.presentation.request.payment.SubmitPayInvoiceRequest request) {
        
        com.clinicmanager.application.dto.payment.PayInvoiceRequest appRequest = com.clinicmanager.application.dto.payment.PayInvoiceRequest.builder()
                .invoiceId(invoiceId)
                .paymentMethod(request.getPaymentMethod())
                .transferCode(request.getTransferCode())
                .build();

        com.clinicmanager.application.dto.payment.PaymentTransactionDto dto = payInvoiceUseCase.payInvoice(appRequest);

        com.clinicmanager.presentation.response.payment.PaymentTransactionResponse response = com.clinicmanager.presentation.response.payment.PaymentTransactionResponse.builder()
                .id(dto.getId())
                .invoiceId(dto.getInvoiceId())
                .paymentMethod(dto.getPaymentMethod().name())
                .amount(dto.getAmount())
                .transactionTime(dto.getTransactionTime())
                .transferCode(dto.getTransferCode())
                .status(dto.getStatus().name())
                .build();

        return ApiResponse.success(response, "Yêu cầu thanh toán hóa đơn thành công.");
    }

    @PostMapping("/transactions/{transactionId}/confirm")
    public ApiResponse<InvoiceResponse> confirm(@PathVariable java.util.UUID transactionId) {
        InvoiceDto dto = confirmPaymentUseCase.confirmPayment(transactionId);
        InvoiceResponse response = mapToResponse(dto);
        return ApiResponse.success(response, "Xác nhận thanh toán thành công.");
    }

    @GetMapping("/{invoiceId}/print")
    public ApiResponse<com.clinicmanager.presentation.response.payment.InvoicePrintResponse> print(@PathVariable java.util.UUID invoiceId) {
        com.clinicmanager.application.dto.payment.InvoicePrintDto dto = printInvoiceUseCase.getInvoicePrintDetails(invoiceId);
        
        com.clinicmanager.presentation.response.payment.InvoicePrintResponse response = com.clinicmanager.presentation.response.payment.InvoicePrintResponse.builder()
                .invoiceId(dto.getInvoiceId())
                .invoiceNumber(dto.getInvoiceNumber())
                .medicalSlipId(dto.getMedicalSlipId())
                .patientName(dto.getPatientName())
                .patientPhone(dto.getPatientPhone())
                .patientAddress(dto.getPatientAddress())
                .examinationDate(dto.getExaminationDate())
                .examinationService(dto.getExaminationService())
                .labTests(dto.getLabTests())
                .medicines(dto.getMedicines())
                .totalAmount(dto.getTotalAmount())
                .totalAmountInWords(dto.getTotalAmountInWords())
                .status(dto.getStatus())
                .paymentMethod(dto.getPaymentMethod())
                .paymentTime(dto.getPaymentTime())
                .build();
                
        return ApiResponse.success(response, "Lấy thông tin in hóa đơn thành công.");
    }

    private InvoiceResponse mapToResponse(InvoiceDto dto) {
        return InvoiceResponse.builder()
                .id(dto.getId())
                .invoiceNumber(dto.getInvoiceNumber())
                .medicalSlipId(dto.getMedicalSlipId())
                .patientId(dto.getPatientId())
                .clinicalExamAmount(dto.getClinicalExamAmount())
                .labTestAmount(dto.getLabTestAmount())
                .medicineAmount(dto.getMedicineAmount())
                .totalAmount(dto.getTotalAmount())
                .status(dto.getStatus().name())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
