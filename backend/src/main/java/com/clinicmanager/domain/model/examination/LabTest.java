package com.clinicmanager.domain.model.examination;

import com.clinicmanager.domain.exception.examination.InvalidLabTestDataException;
import java.time.LocalDate;
import java.util.UUID;

public class LabTest {
    private final UUID id;
    private final String testType;
    private final String result;
    private final LocalDate testDate;
    private final UUID medicalSlipId;

    public LabTest(UUID id, String testType, String result, LocalDate testDate, UUID medicalSlipId) {
        this.id = id != null ? id : UUID.randomUUID();
        this.testType = testType;
        this.result = result;
        this.testDate = testDate;
        this.medicalSlipId = medicalSlipId;
        validate();
    }

    private void validate() {
        if (testType == null || testType.trim().isEmpty()) {
            throw new InvalidLabTestDataException("Loại xét nghiệm không được để trống.");
        }
        if (medicalSlipId == null) {
            throw new InvalidLabTestDataException("Mã phiếu khám không được để trống.");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getTestType() {
        return testType;
    }

    public String getResult() {
        return result;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public UUID getMedicalSlipId() {
        return medicalSlipId;
    }

    public LabTest perform(LocalDate testDate) {
        if (testDate == null) {
            throw new InvalidLabTestDataException("Ngày thực hiện xét nghiệm không được để trống.");
        }
        return new LabTest(this.id, this.testType, this.result, testDate, this.medicalSlipId);
    }

    public LabTest updateResult(String result) {
        if (this.testDate == null) {
            throw new InvalidLabTestDataException("Không thể cập nhật kết quả cho xét nghiệm chưa được thực hiện.");
        }
        if (result == null || result.trim().isEmpty()) {
            throw new InvalidLabTestDataException("Kết quả xét nghiệm không được để trống.");
        }
        return new LabTest(this.id, this.testType, result, this.testDate, this.medicalSlipId);
    }
}
