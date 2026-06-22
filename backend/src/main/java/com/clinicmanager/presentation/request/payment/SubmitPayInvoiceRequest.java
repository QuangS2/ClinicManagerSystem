package com.clinicmanager.presentation.request.payment;

import com.clinicmanager.domain.model.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitPayInvoiceRequest {

    @NotNull(message = "Phương thức thanh toán không được để trống.")
    private PaymentMethod paymentMethod;

    private String transferCode;
}
