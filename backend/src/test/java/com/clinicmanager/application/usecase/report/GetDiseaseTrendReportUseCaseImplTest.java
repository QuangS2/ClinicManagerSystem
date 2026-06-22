package com.clinicmanager.application.usecase.report;

import com.clinicmanager.application.dto.report.DiseaseTrendDto;
import com.clinicmanager.application.dto.report.DiseaseTrendReportDto;
import com.clinicmanager.application.mapper.report.ReportMapper;
import com.clinicmanager.application.port.output.examination.MedicalRecordRepositoryPort;
import com.clinicmanager.domain.exception.report.InvalidReportDateRangeException;
import com.clinicmanager.domain.model.examination.MedicalRecord;
import com.clinicmanager.domain.model.report.DiseaseTrend;
import com.clinicmanager.domain.model.report.DiseaseTrendReport;
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
class GetDiseaseTrendReportUseCaseImplTest {

    @Mock
    private MedicalRecordRepositoryPort medicalRecordRepositoryPort;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private GetDiseaseTrendReportUseCaseImpl getDiseaseTrendReportUseCase;

    private LocalDate startDate;
    private LocalDate endDate;
    private List<MedicalRecord> records;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.now().minusDays(5);
        endDate = LocalDate.now();
        records = new ArrayList<>();

        UUID slipId = UUID.randomUUID();

        // 1. Viêm họng cấp (Day 1)
        records.add(new MedicalRecord(UUID.randomUUID(), "Viêm họng cấp", startDate, "Ghi chú 1", slipId));
        // 2. viêm họng cấp (Day 3 - lowercase, should merge)
        records.add(new MedicalRecord(UUID.randomUUID(), "viêm họng cấp", startDate.plusDays(2), "Ghi chú 2", slipId));
        // 3. Sốt xuất huyết (Day 3)
        records.add(new MedicalRecord(UUID.randomUUID(), "Sốt xuất huyết", startDate.plusDays(2), "Ghi chú 3", slipId));
        // 4. Sốt xuất huyết (Day 6 - with trailing spaces, should trim and merge)
        records.add(new MedicalRecord(UUID.randomUUID(), "Sốt xuất huyết ", endDate, "Ghi chú 4", slipId));
        // 5. Cảm cúm (Day 6)
        records.add(new MedicalRecord(UUID.randomUUID(), "Cảm cúm", endDate, "Ghi chú 5", slipId));
    }

    @Test
    void getReport_Success() {
        // Arrange
        when(medicalRecordRepositoryPort.findByDateRange(startDate, endDate)).thenReturn(records);

        when(reportMapper.toDto(any(DiseaseTrendReport.class))).thenAnswer(invocation -> {
            DiseaseTrendReport report = invocation.getArgument(0);
            List<DiseaseTrendDto> trendDtos = report.getTrends().stream()
                    .map(t -> new DiseaseTrendDto(t.getDiseaseName(), t.getCaseCount(), t.getPercentage()))
                    .toList();
            return new DiseaseTrendReportDto(
                    report.getStartDate(),
                    report.getEndDate(),
                    report.getTotalCases(),
                    trendDtos
            );
        });

        // Act
        DiseaseTrendReportDto result = getDiseaseTrendReportUseCase.getReport(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(5, result.getTotalCases());

        // Trends size should be 3: Sốt xuất huyết, Viêm họng cấp, Cảm cúm
        assertEquals(3, result.getTrends().size());

        // Top 1: Sốt xuất huyết (2 cases, 40%) - 'S' comes before 'V' alphabetically
        DiseaseTrendDto top1 = result.getTrends().get(0);
        assertEquals("Sốt xuất huyết", top1.getDiseaseName());
        assertEquals(2, top1.getCaseCount());
        assertEquals(40.0, top1.getPercentage());

        // Top 2: Viêm họng cấp (2 cases, 40%)
        DiseaseTrendDto top2 = result.getTrends().get(1);
        assertEquals("Viêm họng cấp", top2.getDiseaseName());
        assertEquals(2, top2.getCaseCount());
        assertEquals(40.0, top2.getPercentage());

        // Top 3: Cảm cúm (1 case, 20%)
        DiseaseTrendDto top3 = result.getTrends().get(2);
        assertEquals("Cảm cúm", top3.getDiseaseName());
        assertEquals(1, top3.getCaseCount());
        assertEquals(20.0, top3.getPercentage());
    }

    @Test
    void getReport_ThrowsInvalidReportDateRangeException_WhenStartDateAfterEndDate() {
        // Act & Assert
        assertThrows(InvalidReportDateRangeException.class, () ->
                getDiseaseTrendReportUseCase.getReport(endDate, startDate));
    }

    @Test
    void getReport_Success_DefaultDates() {
        // Arrange
        LocalDate defaultStart = LocalDate.now().withDayOfMonth(1);
        LocalDate defaultEnd = LocalDate.now();

        when(medicalRecordRepositoryPort.findByDateRange(defaultStart, defaultEnd)).thenReturn(Collections.emptyList());

        when(reportMapper.toDto(any(DiseaseTrendReport.class))).thenAnswer(invocation -> {
            DiseaseTrendReport report = invocation.getArgument(0);
            return new DiseaseTrendReportDto(
                    report.getStartDate(),
                    report.getEndDate(),
                    report.getTotalCases(),
                    Collections.emptyList()
            );
        });

        // Act
        DiseaseTrendReportDto result = getDiseaseTrendReportUseCase.getReport(null, null);

        // Assert
        assertNotNull(result);
        assertEquals(defaultStart, result.getStartDate());
        assertEquals(defaultEnd, result.getEndDate());
        assertEquals(0, result.getTotalCases());
        assertTrue(result.getTrends().isEmpty());
    }
}
