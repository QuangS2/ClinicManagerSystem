package com.clinicmanager.presentation.request.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitCreateInvoiceRequest {

    @NotNull(message = "Mã phiếu khám không được để trống.")
    private UUID medicalSlipId;
}
