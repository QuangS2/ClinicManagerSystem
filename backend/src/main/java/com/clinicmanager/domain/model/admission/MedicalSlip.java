package com.clinicmanager.domain.model.admission;

import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import java.time.LocalDate;
import java.util.UUID;

public class MedicalSlip {
    private final UUID id;
    private final LocalDate examinationDate;
    private final MedicalSlipStatus status;
    private final UUID patientId;

    // Clinical Information
    private final String symptoms;
    private final Integer pulse;
    private final Double temperature;
    private final String bloodPressure;
    private final Double weight;
    private final Double height;
    private final String diagnosis;

    public MedicalSlip(UUID id, LocalDate examinationDate, MedicalSlipStatus status, UUID patientId,
                       String symptoms, Integer pulse, Double temperature, String bloodPressure,
                       Double weight, Double height, String diagnosis) {
        this.id = id != null ? id : UUID.randomUUID();
        this.examinationDate = examinationDate;
        this.status = status != null ? status : MedicalSlipStatus.WAITING;
        this.patientId = patientId;
        this.symptoms = symptoms;
        this.pulse = pulse;
        this.temperature = temperature;
        this.bloodPressure = bloodPressure;
        this.weight = weight;
        this.height = height;
        this.diagnosis = diagnosis;
        validate(id == null);
    }

    private void validate(boolean isNew) {
        if (examinationDate == null) {
            throw new InvalidMedicalSlipDataException("Ngày khám không được để trống.");
        }
        if (isNew && examinationDate.isBefore(LocalDate.now())) {
            throw new InvalidMedicalSlipDataException("Ngày khám không được ở quá khứ.");
        }
        if (patientId == null) {
            throw new InvalidMedicalSlipDataException("Mã bệnh nhân không được để trống.");
        }
    }

    public MedicalSlip startExamination(String symptoms, Integer pulse, Double temperature, String bloodPressure,
                                        Double weight, Double height) {
        if (this.status == MedicalSlipStatus.COMPLETED) {
            throw new InvalidMedicalSlipDataException("Không thể khám lâm sàng cho phiếu khám đã hoàn thành.");
        }
        if (this.status == MedicalSlipStatus.CANCELLED) {
            throw new InvalidMedicalSlipDataException("Không thể khám lâm sàng cho phiếu khám đã hủy.");
        }

        if (pulse != null && pulse <= 0) {
            throw new InvalidMedicalSlipDataException("Chỉ số mạch phải lớn hơn 0.");
        }
        if (temperature != null && (temperature < 30 || temperature > 45)) {
            throw new InvalidMedicalSlipDataException("Nhiệt độ cơ thể không hợp lệ (phải từ 30°C đến 45°C).");
        }
        if (weight != null && weight <= 0) {
            throw new InvalidMedicalSlipDataException("Cân nặng phải lớn hơn 0.");
        }
        if (height != null && height <= 0) {
            throw new InvalidMedicalSlipDataException("Chiều cao phải lớn hơn 0.");
        }

        return new MedicalSlip(this.id, this.examinationDate, MedicalSlipStatus.EXAMINING, this.patientId,
                symptoms, pulse, temperature, bloodPressure, weight, height, this.diagnosis);
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getExaminationDate() {
        return examinationDate;
    }

    public MedicalSlipStatus getStatus() {
        return status;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public Integer getPulse() {
        return pulse;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getHeight() {
        return height;
    }

    public MedicalSlip diagnose(String diagnosis) {
        if (this.status != MedicalSlipStatus.EXAMINING) {
            throw new InvalidMedicalSlipDataException("Chỉ có thể chẩn đoán bệnh khi phiếu khám ở trạng thái EXAMINING.");
        }
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new InvalidMedicalSlipDataException("Chẩn đoán không được để trống.");
        }
        return new MedicalSlip(this.id, this.examinationDate, this.status, this.patientId,
                this.symptoms, this.pulse, this.temperature, this.bloodPressure, this.weight, this.height, diagnosis);
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public MedicalSlip complete() {
        if (this.status != MedicalSlipStatus.EXAMINING) {
            throw new InvalidMedicalSlipDataException("Chỉ có thể hoàn thành phiếu khám khi ở trạng thái EXAMINING.");
        }
        if (this.diagnosis == null || this.diagnosis.trim().isEmpty()) {
            throw new InvalidMedicalSlipDataException("Chỉ có thể hoàn thành phiếu khám khi đã có chẩn đoán bệnh.");
        }
        return new MedicalSlip(this.id, this.examinationDate, MedicalSlipStatus.COMPLETED, this.patientId,
                this.symptoms, this.pulse, this.temperature, this.bloodPressure, this.weight, this.height, this.diagnosis);
    }
}
