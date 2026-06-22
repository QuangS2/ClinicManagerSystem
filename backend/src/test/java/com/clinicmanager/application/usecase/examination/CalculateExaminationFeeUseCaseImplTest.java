package com.clinicmanager.application.usecase.examination;

import com.clinicmanager.application.dto.examination.ExaminationFeeBreakdownDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateExaminationFeeUseCaseImplTest {

    @Mock
    private MedicalSlipRepositoryPort medicalSlipRepositoryPort;

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    @Mock
    private AppointmentRepositoryPort appointmentRepositoryPort;

    @Mock
    private MedicalServiceRepositoryPort medicalServiceRepositoryPort;

    @Mock
    private LabTestRepositoryPort labTestRepositoryPort;

    @Mock
    private PrescriptionRepositoryPort prescriptionRepositoryPort;

    @Mock
    private MedicineRepositoryPort medicineRepositoryPort;

    @InjectMocks
    private CalculateExaminationFeeUseCaseImpl calculateExaminationFeeUseCase;

    private UUID medicalSlipId;
    private UUID patientId;
    private UUID appointmentId;
    private UUID serviceId;
    private UUID medicineId;

    private MedicalSlip completedSlip;
    private MedicalSlip examiningSlip;
    private Patient patient;
    private Appointment appointment;
    private MedicalService medicalService;
    private LabTest labTest;
    private Medicine medicine;
    private Prescription prescription;

    @BeforeEach
    void setUp() {
        medicalSlipId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        medicineId = UUID.randomUUID();

        completedSlip = new MedicalSlip(
                medicalSlipId,
                LocalDate.now(),
                MedicalSlipStatus.COMPLETED,
                patientId,
                "Sot ho", 85, 38.0, "120/80", 55.0, 160.0, "Viem hong"
        );

        examiningSlip = new MedicalSlip(
                medicalSlipId,
                LocalDate.now(),
                MedicalSlipStatus.EXAMINING,
                patientId,
                "Sot ho", 85, 38.0, "120/80", 55.0, 160.0, "Viem hong"
        );

        patient = new Patient(
                patientId,
                "Nguyen Van A",
                LocalDate.of(1990, 1, 1),
                "Nam",
                "0987654321",
                "Ha Noi",
                "a@gmail.com"
        );

        appointment = new Appointment(
                appointmentId,
                LocalDate.now(),
                LocalTime.of(9, 0),
                AppointmentStatus.COMPLETED,
                patientId,
                serviceId
        );

        medicalService = new MedicalService(
                serviceId,
                "Kham tong quat",
                BigDecimal.valueOf(120000),
                "Mo ta"
        );

        labTest = new LabTest(
                UUID.randomUUID(),
                "Xet nghiem mau",
                "Binh thuong",
                LocalDate.now(),
                medicalSlipId
        );

        medicine = new Medicine(
                medicineId,
                "Paracetamol 500mg",
                "Vien",
                BigDecimal.valueOf(1500),
                100
        );

        PrescriptionItem prescriptionItem = new PrescriptionItem(
                medicineId,
                10,
                "Uong sau an"
        );

        prescription = new Prescription(
                UUID.randomUUID(),
                LocalDate.now(),
                medicalSlipId,
                List.of(prescriptionItem)
        );
    }

    @Test
    void calculate_Success_WithAppointment() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(completedSlip));
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepositoryPort.findByPatientIdAndDate(patientId, completedSlip.getExaminationDate()))
                .thenReturn(List.of(appointment));
        when(medicalServiceRepositoryPort.findById(serviceId)).thenReturn(Optional.of(medicalService));
        when(labTestRepositoryPort.findByMedicalSlipId(medicalSlipId)).thenReturn(List.of(labTest));
        when(medicalServiceRepositoryPort.findByName("Xet nghiem mau")).thenReturn(Optional.of(new MedicalService(
                UUID.randomUUID(), "Xet nghiem mau", BigDecimal.valueOf(150000), "Mo ta")));
        when(prescriptionRepositoryPort.findByMedicalSlipId(medicalSlipId)).thenReturn(Optional.of(prescription));
        when(medicineRepositoryPort.findById(medicineId)).thenReturn(Optional.of(medicine));

        ExaminationFeeBreakdownDto result = calculateExaminationFeeUseCase.calculate(medicalSlipId);

        assertNotNull(result);
        assertEquals(medicalSlipId, result.getMedicalSlipId());
        assertEquals(patientId, result.getPatientId());
        assertEquals("Nguyen Van A", result.getPatientName());

        // Phí khám
        assertEquals("Kham tong quat", result.getExaminationService().getServiceName());
        assertEquals(BigDecimal.valueOf(120000), result.getExaminationService().getUnitPrice());

        // Phí xét nghiệm
        assertEquals(1, result.getLabTests().size());
        assertEquals("Xet nghiem mau", result.getLabTests().get(0).getServiceName());
        assertEquals(BigDecimal.valueOf(150000), result.getLabTests().get(0).getUnitPrice());

        // Phí thuốc
        assertEquals(1, result.getMedicines().size());
        assertEquals("Paracetamol 500mg", result.getMedicines().get(0).getMedicineName());
        assertEquals(BigDecimal.valueOf(1500), result.getMedicines().get(0).getUnitPrice());
        assertEquals(10, result.getMedicines().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(15000), result.getMedicines().get(0).getSubtotal());

        // Tổng tiền: 120,000 + 150,000 + 15,000 = 285,000
        assertEquals(BigDecimal.valueOf(285000), result.getTotalAmount());
    }

    @Test
    void calculate_Success_WalkIn_WithDefaultService() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(completedSlip));
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepositoryPort.findByPatientIdAndDate(patientId, completedSlip.getExaminationDate()))
                .thenReturn(Collections.emptyList());
        when(medicalServiceRepositoryPort.findByName("Khám lâm sàng")).thenReturn(Optional.of(new MedicalService(
                UUID.randomUUID(), "Khám lâm sàng", BigDecimal.valueOf(60000), "Mo ta")));
        when(labTestRepositoryPort.findByMedicalSlipId(medicalSlipId)).thenReturn(Collections.emptyList());
        when(prescriptionRepositoryPort.findByMedicalSlipId(medicalSlipId)).thenReturn(Optional.empty());

        ExaminationFeeBreakdownDto result = calculateExaminationFeeUseCase.calculate(medicalSlipId);

        assertNotNull(result);
        assertEquals("Khám lâm sàng", result.getExaminationService().getServiceName());
        assertEquals(BigDecimal.valueOf(60000), result.getExaminationService().getUnitPrice());
        assertEquals(BigDecimal.valueOf(60000), result.getTotalAmount());
    }

    @Test
    void calculate_Success_WalkIn_WithFallback() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(completedSlip));
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepositoryPort.findByPatientIdAndDate(patientId, completedSlip.getExaminationDate()))
                .thenReturn(Collections.emptyList());
        when(medicalServiceRepositoryPort.findByName("Khám lâm sàng")).thenReturn(Optional.empty());
        when(medicalServiceRepositoryPort.findByName("Khám thường")).thenReturn(Optional.empty());
        when(labTestRepositoryPort.findByMedicalSlipId(medicalSlipId)).thenReturn(Collections.emptyList());
        when(prescriptionRepositoryPort.findByMedicalSlipId(medicalSlipId)).thenReturn(Optional.empty());

        ExaminationFeeBreakdownDto result = calculateExaminationFeeUseCase.calculate(medicalSlipId);

        assertNotNull(result);
        assertEquals("Dịch vụ khám lâm sàng", result.getExaminationService().getServiceName());
        assertEquals(BigDecimal.valueOf(50000), result.getExaminationService().getUnitPrice());
        assertEquals(BigDecimal.valueOf(50000), result.getTotalAmount());
    }

    @Test
    void calculate_ThrowsMedicalSlipNotFoundException_WhenSlipDoesNotExist() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.empty());

        assertThrows(MedicalSlipNotFoundException.class, () -> calculateExaminationFeeUseCase.calculate(medicalSlipId));
    }

    @Test
    void calculate_ThrowsInvalidMedicalSlipDataException_WhenSlipIsNotCompleted() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(examiningSlip));

        assertThrows(InvalidMedicalSlipDataException.class, () -> calculateExaminationFeeUseCase.calculate(medicalSlipId));
    }

    @Test
    void calculate_ThrowsPatientNotFoundException_WhenPatientDoesNotExist() {
        when(medicalSlipRepositoryPort.findById(medicalSlipId)).thenReturn(Optional.of(completedSlip));
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> calculateExaminationFeeUseCase.calculate(medicalSlipId));
    }
}
