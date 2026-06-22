package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.examination.ExaminationFeeBreakdownDto;
import com.clinicmanager.application.port.input.examination.CalculateExaminationFeeUseCase;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.appointment.AppointmentRepositoryPort;
import com.clinicmanager.application.port.output.examination.LabTestRepositoryPort;
import com.clinicmanager.application.port.output.examination.PrescriptionRepositoryPort;
import com.clinicmanager.application.port.output.medicine.MedicineRepositoryPort;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.application.port.output.service.MedicalServiceRepositoryPort;
import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
import com.clinicmanager.domain.exception.patient.PatientNotFoundException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.appointment.Appointment;
import com.clinicmanager.domain.model.appointment.AppointmentStatus;
import com.clinicmanager.domain.model.examination.LabTest;
import com.clinicmanager.domain.model.examination.Prescription;
import com.clinicmanager.domain.model.examination.PrescriptionItem;
import com.clinicmanager.domain.model.medicine.Medicine;
import com.clinicmanager.domain.model.patient.Patient;
import com.clinicmanager.domain.model.service.MedicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalculateExaminationFeeUseCaseImpl implements CalculateExaminationFeeUseCase {

    private final MedicalSlipRepositoryPort medicalSlipRepositoryPort;
    private final PatientRepositoryPort patientRepositoryPort;
    private final AppointmentRepositoryPort appointmentRepositoryPort;
    private final MedicalServiceRepositoryPort medicalServiceRepositoryPort;
    private final LabTestRepositoryPort labTestRepositoryPort;
    private final PrescriptionRepositoryPort prescriptionRepositoryPort;
    private final MedicineRepositoryPort medicineRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public ExaminationFeeBreakdownDto calculate(UUID medicalSlipId) {
        MedicalSlip medicalSlip = medicalSlipRepositoryPort.findById(medicalSlipId)
                .orElseThrow(() -> new MedicalSlipNotFoundException("Phiếu khám không tồn tại."));

        if (medicalSlip.getStatus() != MedicalSlipStatus.COMPLETED) {
            throw new InvalidMedicalSlipDataException("Chỉ có thể tính chi phí khám cho phiếu khám đã hoàn thành.");
        }

        Patient patient = patientRepositoryPort.findById(medicalSlip.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Bệnh nhân không tồn tại."));

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 1. Phí khám lâm sàng (dựa trên lịch hẹn hoặc mặc định)
        List<Appointment> appointments = appointmentRepositoryPort.findByPatientIdAndDate(
                patient.getId(), medicalSlip.getExaminationDate());

        Optional<Appointment> completedAppointment = appointments.stream()
                .filter(app -> app.getStatus() == AppointmentStatus.COMPLETED)
                .findFirst();

        String examServiceName = "Dịch vụ khám lâm sàng";
        BigDecimal examServicePrice = BigDecimal.valueOf(50000); // Giá mặc định phòng khám

        if (completedAppointment.isPresent()) {
            Optional<MedicalService> serviceOpt = medicalServiceRepositoryPort.findById(completedAppointment.get().getServiceId());
            if (serviceOpt.isPresent()) {
                examServiceName = serviceOpt.get().getName();
                examServicePrice = serviceOpt.get().getPrice();
            }
        } else {
            // Tìm dịch vụ mặc định theo tên
            Optional<MedicalService> defaultService = medicalServiceRepositoryPort.findByName("Khám lâm sàng")
                    .or(() -> medicalServiceRepositoryPort.findByName("Khám thường"));
            if (defaultService.isPresent()) {
                examServiceName = defaultService.get().getName();
                examServicePrice = defaultService.get().getPrice();
            }
        }

        ExaminationFeeBreakdownDto.ServiceItem examServiceItem = ExaminationFeeBreakdownDto.ServiceItem.builder()
                .serviceName(examServiceName)
                .unitPrice(examServicePrice)
                .quantity(1)
                .subtotal(examServicePrice)
                .build();

        totalAmount = totalAmount.add(examServicePrice);

        // 2. Phí xét nghiệm cận lâm sàng
        List<LabTest> labTests = labTestRepositoryPort.findByMedicalSlipId(medicalSlipId);
        List<ExaminationFeeBreakdownDto.ServiceItem> labTestServiceItems = new ArrayList<>();

        for (LabTest test : labTests) {
            Optional<MedicalService> serviceOpt = medicalServiceRepositoryPort.findByName(test.getTestType());
            BigDecimal testPrice = serviceOpt.map(MedicalService::getPrice).orElse(BigDecimal.ZERO);
            String serviceName = serviceOpt.map(MedicalService::getName).orElse(test.getTestType());

            ExaminationFeeBreakdownDto.ServiceItem testItem = ExaminationFeeBreakdownDto.ServiceItem.builder()
                    .serviceName(serviceName)
                    .unitPrice(testPrice)
                    .quantity(1)
                    .subtotal(testPrice)
                    .build();

            labTestServiceItems.add(testItem);
            totalAmount = totalAmount.add(testPrice);
        }

        // 3. Phí thuốc kê đơn
        List<ExaminationFeeBreakdownDto.MedicineItem> medicineItems = new ArrayList<>();
        Optional<Prescription> prescriptionOpt = prescriptionRepositoryPort.findByMedicalSlipId(medicalSlipId);

        if (prescriptionOpt.isPresent()) {
            Prescription prescription = prescriptionOpt.get();
            for (PrescriptionItem item : prescription.getItems()) {
                Optional<Medicine> medicineOpt = medicineRepositoryPort.findById(item.getMedicineId());
                String medName = medicineOpt.map(Medicine::getName).orElse("Thuốc đã xóa");
                String unit = medicineOpt.map(Medicine::getUnit).orElse("Đơn vị");
                BigDecimal medPrice = medicineOpt.map(Medicine::getPrice).orElse(BigDecimal.ZERO);
                BigDecimal subtotal = medPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

                ExaminationFeeBreakdownDto.MedicineItem medItem = ExaminationFeeBreakdownDto.MedicineItem.builder()
                        .medicineName(medName)
                        .unit(unit)
                        .unitPrice(medPrice)
                        .quantity(item.getQuantity())
                        .subtotal(subtotal)
                        .build();

                medicineItems.add(medItem);
                totalAmount = totalAmount.add(subtotal);
            }
        }

        return ExaminationFeeBreakdownDto.builder()
                .medicalSlipId(medicalSlipId)
                .patientId(patient.getId())
                .patientName(patient.getFullName())
                .examinationService(examServiceItem)
                .labTests(labTestServiceItems)
                .medicines(medicineItems)
                .totalAmount(totalAmount)
                .build();
    }
}
