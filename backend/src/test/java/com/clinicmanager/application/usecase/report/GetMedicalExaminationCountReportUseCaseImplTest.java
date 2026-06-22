package com.clinicmanager.application.usecase.report;

import com.clinicmanager.application.dto.report.DailyExaminationCountDto;
import com.clinicmanager.application.dto.report.MedicalExaminationCountReportDto;
import com.clinicmanager.application.mapper.report.ReportMapper;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.domain.exception.report.InvalidReportDateRangeException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.report.MedicalExaminationCountReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMedicalExaminationCountReportUseCaseImplTest {

    @Mock
    private MedicalSlipRepositoryPort medicalSlipRepositoryPort;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private GetMedicalExaminationCountReportUseCaseImpl getMedicalExaminationCountReportUseCase;

    private List<MedicalSlip> medicalSlips;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.now().minusDays(5);
        endDate = LocalDate.now();

        medicalSlips = new ArrayList<>();
        // 2 slips on day 1 (startDate)
        medicalSlips.add(new MedicalSlip(UUID.randomUUID(), startDate, MedicalSlipStatus.COMPLETED, UUID.randomUUID(), "Symptoms", 80, 36.5, "120/80", 60.0, 170.0, "Diagnosis"));
        medicalSlips.add(new MedicalSlip(UUID.randomUUID(), startDate, MedicalSlipStatus.WAITING, UUID.randomUUID(), "Symptoms", 80, 36.5, "120/80", 60.0, 170.0, null));
        // 1 slip on day 3
        medicalSlips.add(new MedicalSlip(UUID.randomUUID(), startDate.plusDays(2), MedicalSlipStatus.EXAMINING, UUID.randomUUID(), "Symptoms", 80, 36.5, "120/80", 60.0, 170.0, "Diagnosis"));
        // 1 slip on day 5 (endDate)
        medicalSlips.add(new MedicalSlip(UUID.randomUUID(), endDate, MedicalSlipStatus.CANCELLED, UUID.randomUUID(), "Symptoms", 80, 36.5, "120/80", 60.0, 170.0, null));
    }

    @Test
    void getReport_Success() {
        // Arrange
        when(medicalSlipRepositoryPort.findByDateRange(startDate, endDate)).thenReturn(medicalSlips);
        when(reportMapper.toDto(any(MedicalExaminationCountReport.class))).thenAnswer(invocation -> {
            MedicalExaminationCountReport report = invocation.getArgument(0);
            List<DailyExaminationCountDto> dailyDtos = report.getDailyCounts().stream()
                    .map(d -> new DailyExaminationCountDto(d.getDate(), d.getCount()))
                    .toList();
            return new MedicalExaminationCountReportDto(
                    report.getStartDate(),
                    report.getEndDate(),
                    dailyDtos,
                    report.getTotalExaminations(),
                    report.getStatusBreakdown(),
                    report.getAveragePerDay()
            );
        });

        // Act
        MedicalExaminationCountReportDto result = getMedicalExaminationCountReportUseCase.getReport(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(4, result.getTotalExaminations());

        // Chrono unit days between startDate and endDate is 5 days, so 6 days inclusive (start, start+1, start+2, start+3, start+4, start+5)
        assertEquals(6, result.getDailyCounts().size());
        assertEquals(2, result.getDailyCounts().get(0).getCount()); // startDate has 2
        assertEquals(0, result.getDailyCounts().get(1).getCount()); // startDate + 1 has 0
        assertEquals(1, result.getDailyCounts().get(2).getCount()); // startDate + 2 has 1
        assertEquals(0, result.getDailyCounts().get(3).getCount()); // startDate + 3 has 0
        assertEquals(0, result.getDailyCounts().get(4).getCount()); // startDate + 4 has 0
        assertEquals(1, result.getDailyCounts().get(5).getCount()); // startDate + 5 (endDate) has 1

        // Average = 4 examinations / 6 days = 0.6666...
        assertEquals(4.0 / 6.0, result.getAveragePerDay(), 0.001);

        // Status breakdown verification
        assertEquals(1L, result.getStatusBreakdown().get("COMPLETED"));
        assertEquals(1L, result.getStatusBreakdown().get("WAITING"));
        assertEquals(1L, result.getStatusBreakdown().get("EXAMINING"));
        assertEquals(1L, result.getStatusBreakdown().get("CANCELLED"));
    }

    @Test
    void getReport_ThrowsInvalidReportDateRangeException_WhenStartDateAfterEndDate() {
        // Act & Assert
        assertThrows(InvalidReportDateRangeException.class, () -> 
                getMedicalExaminationCountReportUseCase.getReport(endDate, startDate));
    }

    @Test
    void getReport_Success_DefaultDates() {
        // Arrange
        LocalDate defaultStart = LocalDate.now().withDayOfMonth(1);
        LocalDate defaultEnd = LocalDate.now();

        when(medicalSlipRepositoryPort.findByDateRange(defaultStart, defaultEnd)).thenReturn(Collections.emptyList());
        when(reportMapper.toDto(any(MedicalExaminationCountReport.class))).thenAnswer(invocation -> {
            MedicalExaminationCountReport report = invocation.getArgument(0);
            List<DailyExaminationCountDto> dailyDtos = report.getDailyCounts().stream()
                    .map(d -> new DailyExaminationCountDto(d.getDate(), d.getCount()))
                    .toList();
            return new MedicalExaminationCountReportDto(
                    report.getStartDate(),
                    report.getEndDate(),
                    dailyDtos,
                    report.getTotalExaminations(),
                    report.getStatusBreakdown(),
                    report.getAveragePerDay()
            );
        });

        // Act
        MedicalExaminationCountReportDto result = getMedicalExaminationCountReportUseCase.getReport(null, null);

        // Assert
        assertNotNull(result);
        assertEquals(defaultStart, result.getStartDate());
        assertEquals(defaultEnd, result.getEndDate());
        assertEquals(0, result.getTotalExaminations());
        assertEquals(0.0, result.getAveragePerDay());
    }
}
