package com.clinicmanager.application.usecase.report;

import com.clinicmanager.application.dto.report.DailyExaminationRatioDto;
import com.clinicmanager.application.dto.report.MedicalExaminationRatioReportDto;
import com.clinicmanager.application.mapper.report.ReportMapper;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.examination.LabTestRepositoryPort;
import com.clinicmanager.application.port.output.examination.PrescriptionRepositoryPort;
import com.clinicmanager.domain.exception.report.InvalidReportDateRangeException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.examination.LabTest;
import com.clinicmanager.domain.model.examination.Prescription;
import com.clinicmanager.domain.model.examination.PrescriptionItem;
import com.clinicmanager.domain.model.report.DailyExaminationRatio;
import com.clinicmanager.domain.model.report.MedicalExaminationRatioReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMedicalExaminationRatioReportUseCaseImplTest {

    @Mock
    private MedicalSlipRepositoryPort medicalSlipRepositoryPort;

    @Mock
    private LabTestRepositoryPort labTestRepositoryPort;

    @Mock
    private PrescriptionRepositoryPort prescriptionRepositoryPort;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private GetMedicalExaminationRatioReportUseCaseImpl getMedicalExaminationRatioReportUseCase;

    private LocalDate startDate;
    private LocalDate endDate;
    private List<MedicalSlip> slips;
    private List<LabTest> labTests;
    private List<Prescription> prescriptions;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.now().minusDays(3);
        endDate = LocalDate.now();

        slips = new ArrayList<>();
        labTests = new ArrayList<>();
        prescriptions = new ArrayList<>();

        UUID patientId = UUID.randomUUID();

        // Medical Slip 1 (startDate) - has both Lab Test and Prescription
        UUID slipId1 = UUID.randomUUID();
        slips.add(new MedicalSlip(
                slipId1,
                startDate,
                MedicalSlipStatus.COMPLETED,
                patientId,
                "Sốt, ho",
                80,
                38.5,
                "120/80",
                60.0,
                165.0,
                "Viêm họng"
        ));
        labTests.add(new LabTest(UUID.randomUUID(), "Công thức máu", "Bình thường", startDate, slipId1));
        prescriptions.add(new Prescription(
                UUID.randomUUID(),
                startDate,
                slipId1,
                List.of(new PrescriptionItem(UUID.randomUUID(), 10, "Uống sau ăn"))
        ));

        // Medical Slip 2 (startDate + 1) - only has Lab Test
        UUID slipId2 = UUID.randomUUID();
        slips.add(new MedicalSlip(
                slipId2,
                startDate.plusDays(1),
                MedicalSlipStatus.COMPLETED,
                patientId,
                "Đau bụng",
                75,
                37.0,
                "110/70",
                55.0,
                160.0,
                "Rối loạn tiêu hóa"
        ));
        labTests.add(new LabTest(UUID.randomUUID(), "Siêu âm ổ bụng", "Bình thường", startDate.plusDays(1), slipId2));

        // Medical Slip 3 (startDate + 1) - only has Prescription
        UUID slipId3 = UUID.randomUUID();
        slips.add(new MedicalSlip(
                slipId3,
                startDate.plusDays(1),
                MedicalSlipStatus.COMPLETED,
                patientId,
                "Nhức đầu",
                70,
                36.8,
                "120/80",
                70.0,
                175.0,
                "Suy nhược thần kinh"
        ));
        prescriptions.add(new Prescription(
                UUID.randomUUID(),
                startDate.plusDays(1),
                slipId3,
                List.of(new PrescriptionItem(UUID.randomUUID(), 5, "Uống sáng tối"))
        ));

        // Note: startDate + 2 has no medical slips (missing date)
        // Medical Slip 4 (endDate) - has nothing (normal exam only)
        UUID slipId4 = UUID.randomUUID();
        slips.add(new MedicalSlip(
                slipId4,
                endDate,
                MedicalSlipStatus.COMPLETED,
                patientId,
                "Kiểm tra sức khỏe",
                72,
                36.6,
                "120/80",
                65.0,
                170.0,
                "Khỏe mạnh"
        ));
    }

    @Test
    void getReport_Success() {
        // Arrange
        when(medicalSlipRepositoryPort.findByDateRange(startDate, endDate)).thenReturn(slips);

        when(labTestRepositoryPort.findByMedicalSlipIds(anyList())).thenReturn(labTests);
        when(prescriptionRepositoryPort.findByMedicalSlipIds(anyList())).thenReturn(prescriptions);

        when(reportMapper.toDto(any(MedicalExaminationRatioReport.class))).thenAnswer(invocation -> {
            MedicalExaminationRatioReport report = invocation.getArgument(0);
            List<DailyExaminationRatioDto> dailyDtos = report.getDailyRatios().stream()
                    .map(d -> new DailyExaminationRatioDto(
                            d.getDate(),
                            d.getTotalExaminations(),
                            d.getExaminationsWithLabTests(),
                            d.getExaminationsWithMedicines(),
                            d.getLabTestRatio(),
                            d.getMedicineRatio()
                    ))
                    .toList();
            return new MedicalExaminationRatioReportDto(
                    report.getStartDate(),
                    report.getEndDate(),
                    report.getTotalExaminations(),
                    report.getTotalExaminationsWithLabTests(),
                    report.getTotalExaminationsWithMedicines(),
                    report.getOverallLabTestRatio(),
                    report.getOverallMedicineRatio(),
                    dailyDtos
            );
        });

        // Act
        MedicalExaminationRatioReportDto result = getMedicalExaminationRatioReportUseCase.getReport(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());

        // Overall stats
        assertEquals(4, result.getTotalExaminations());
        assertEquals(2, result.getTotalExaminationsWithLabTests()); // Slip 1 and Slip 2
        assertEquals(2, result.getTotalExaminationsWithMedicines()); // Slip 1 and Slip 3
        assertEquals(50.0, result.getOverallLabTestRatio()); // 2 / 4 * 100
        assertEquals(50.0, result.getOverallMedicineRatio()); // 2 / 4 * 100

        // Daily ratios: 4 days (startDate, startDate+1, startDate+2, endDate)
        assertEquals(4, result.getDailyRatios().size());

        // Day 1 (startDate)
        DailyExaminationRatioDto day1 = result.getDailyRatios().get(0);
        assertEquals(startDate, day1.getDate());
        assertEquals(1, day1.getTotalExaminations());
        assertEquals(1, day1.getExaminationsWithLabTests());
        assertEquals(1, day1.getExaminationsWithMedicines());
        assertEquals(100.0, day1.getLabTestRatio());
        assertEquals(100.0, day1.getMedicineRatio());

        // Day 2 (startDate + 1)
        DailyExaminationRatioDto day2 = result.getDailyRatios().get(1);
        assertEquals(startDate.plusDays(1), day2.getDate());
        assertEquals(2, day2.getTotalExaminations());
        assertEquals(1, day2.getExaminationsWithLabTests());
        assertEquals(1, day2.getExaminationsWithMedicines());
        assertEquals(50.0, day2.getLabTestRatio());
        assertEquals(50.0, day2.getMedicineRatio());

        // Day 3 (startDate + 2 - empty day)
        DailyExaminationRatioDto day3 = result.getDailyRatios().get(2);
        assertEquals(startDate.plusDays(2), day3.getDate());
        assertEquals(0, day3.getTotalExaminations());
        assertEquals(0, day3.getExaminationsWithLabTests());
        assertEquals(0, day3.getExaminationsWithMedicines());
        assertEquals(0.0, day3.getLabTestRatio());
        assertEquals(0.0, day3.getMedicineRatio());

        // Day 4 (endDate)
        DailyExaminationRatioDto day4 = result.getDailyRatios().get(3);
        assertEquals(endDate, day4.getDate());
        assertEquals(1, day4.getTotalExaminations());
        assertEquals(0, day4.getExaminationsWithLabTests());
        assertEquals(0, day4.getExaminationsWithMedicines());
        assertEquals(0.0, day4.getLabTestRatio());
        assertEquals(0.0, day4.getMedicineRatio());
    }

    @Test
    void getReport_ThrowsInvalidReportDateRangeException_WhenStartDateAfterEndDate() {
        // Act & Assert
        assertThrows(InvalidReportDateRangeException.class, () ->
                getMedicalExaminationRatioReportUseCase.getReport(endDate, startDate));
    }

    @Test
    void getReport_Success_DefaultDates() {
        // Arrange
        LocalDate defaultStart = LocalDate.now().withDayOfMonth(1);
        LocalDate defaultEnd = LocalDate.now();

        when(medicalSlipRepositoryPort.findByDateRange(defaultStart, defaultEnd)).thenReturn(Collections.emptyList());
        when(labTestRepositoryPort.findByMedicalSlipIds(anyList())).thenReturn(Collections.emptyList());
        when(prescriptionRepositoryPort.findByMedicalSlipIds(anyList())).thenReturn(Collections.emptyList());

        when(reportMapper.toDto(any(MedicalExaminationRatioReport.class))).thenAnswer(invocation -> {
            MedicalExaminationRatioReport report = invocation.getArgument(0);
            return new MedicalExaminationRatioReportDto(
                    report.getStartDate(),
                    report.getEndDate(),
                    report.getTotalExaminations(),
                    report.getTotalExaminationsWithLabTests(),
                    report.getTotalExaminationsWithMedicines(),
                    report.getOverallLabTestRatio(),
                    report.getOverallMedicineRatio(),
                    Collections.emptyList()
            );
        });

        // Act
        MedicalExaminationRatioReportDto result = getMedicalExaminationRatioReportUseCase.getReport(null, null);

        // Assert
        assertNotNull(result);
        assertEquals(defaultStart, result.getStartDate());
        assertEquals(defaultEnd, result.getEndDate());
        assertEquals(0, result.getTotalExaminations());
        assertEquals(0, result.getTotalExaminationsWithLabTests());
        assertEquals(0, result.getTotalExaminationsWithMedicines());
        assertEquals(0.0, result.getOverallLabTestRatio());
        assertEquals(0.0, result.getOverallMedicineRatio());
    }
}
