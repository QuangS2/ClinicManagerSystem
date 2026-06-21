package com.clinicmanager.presentation.request.examination;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitClinicalExamRequest {

    @Size(max = 1000, message = "Triệu chứng không được vượt quá 1000 ký tự")
    private String symptoms;

    @Min(value = 1, message = "Chỉ số mạch phải lớn hơn 0")
    private Integer pulse;

    @DecimalMin(value = "30.0", message = "Nhiệt độ cơ thể không hợp lệ, phải từ 30°C đến 45°C")
    @DecimalMax(value = "45.0", message = "Nhiệt độ cơ thể không hợp lệ, phải từ 30°C đến 45°C")
    private Double temperature;

    @Size(max = 20, message = "Huyết áp không được vượt quá 20 ký tự")
    private String bloodPressure;

    @DecimalMin(value = "1.0", message = "Cân nặng phải lớn hơn 0")
    private Double weight;

    @DecimalMin(value = "1.0", message = "Chiều cao phải lớn hơn 0")
    private Double height;
}
