package com.clinicmanager.application.dto.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientRequest {
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String phone;
    private String address;
    private String email;
}
