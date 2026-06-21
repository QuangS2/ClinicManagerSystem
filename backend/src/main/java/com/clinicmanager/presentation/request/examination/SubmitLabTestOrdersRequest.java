package com.clinicmanager.presentation.request.examination;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitLabTestOrdersRequest {

    @NotEmpty(message = "Danh sách loại xét nghiệm không được để trống")
    private List<String> testTypes;
}
