package com.clinicmanager.presentation.request.examination;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitUpdateLabTestResultRequest {

    @NotBlank(message = "Kết quả xét nghiệm không được để trống")
    @Size(max = 1000, message = "Kết quả xét nghiệm không được vượt quá 1000 ký tự")
    private String result;
}
