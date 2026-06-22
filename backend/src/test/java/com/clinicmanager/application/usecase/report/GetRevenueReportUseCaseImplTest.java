package com.clinicmanager.application.usecase.report;

import com.clinicmanager.application.dto.report.DailyRevenueDto;
import com.clinicmanager.application.dto.report.RevenueReportDto;
import com.clinicmanager.application.mapper.report.ReportMapper;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.domain.exception.report.InvalidReportDateRangeException;
import com.clinicmanager.domain.model.payment.Invoice;
import com.clinicmanager.domain.model.payment.InvoiceStatus;
import com.clinicmanager.domain.model.report.RevenueReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRevenueReportUseCaseImplTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepositoryPort;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private GetRevenueReportUseCaseImpl getRevenueReportUseCase;

    private List<Invoice> invoices;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.now().minusDays(5);
        endDate = LocalDate.now();

        invoices = new ArrayList<>();

        // Day 1 (startDate)
        invoices.add(new Invoice(
                UUID.randomUUID(),
                "INV-1",
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(150000),
                BigDecimal.valueOf(20000),
                BigDecimal.valueOf(220000),
                InvoiceStatus.PAID,
                startDate.atTime(10, 0)
        ));

        // Day 3
        invoices.add(new Invoice(
                UUID.randomUUID(),
                "INV-2",
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(60000),
                InvoiceStatus.PAID,
                startDate.plusDays(2).atTime(14, 30)
        ));

        // Day 6 (endDate)
        invoices.add(new Invoice(
                UUID.randomUUID(),
                "INV-3",
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(30000),
                BigDecimal.valueOf(180000),
                InvoiceStatus.PAID,
                endDate.atTime(9, 15)
        ));
    }

    @Test
    void getReport_Success() {
        // Arrange
        when(invoiceRepositoryPort.findByDateRangeAndStatus(startDate, endDate, InvoiceStatus.PAID))
                .thenReturn(invoices);

        when(reportMapper.toDto(any(RevenueReport.class))).thenAnswer(invocation -> {
            RevenueReport report = invocation.getArgument(0);
            List<DailyRevenueDto> dailyDtos = report.getDailyRevenues().stream()
                    .map(d -> new DailyRevenueDto(d.getDate(), d.getRevenue()))
                    .toList();
            return new RevenueReportDto(
                    report.getStartDate(),
                    report.getEndDate(),
                    report.getTotalRevenue(),
                    report.getClinicalExamRevenue(),
                    report.getLabTestRevenue(),
                    report.getMedicineRevenue(),
                    dailyDtos,
                    report.getAverageRevenuePerDay()
            );
        });

        // Act
        RevenueReportDto result = getRevenueReportUseCase.getReport(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(0, BigDecimal.valueOf(460000).compareTo(result.getTotalRevenue()));
        assertEquals(0, BigDecimal.valueOf(150000).compareTo(result.getClinicalExamRevenue()));
        assertEquals(0, BigDecimal.valueOf(250000).compareTo(result.getLabTestRevenue()));
        assertEquals(0, BigDecimal.valueOf(60000).compareTo(result.getMedicineRevenue()));

        // Chrono unit days between startDate and endDate is 5 days, so 6 days inclusive
        assertEquals(6, result.getDailyRevenues().size());
        assertEquals(0, BigDecimal.valueOf(220000).compareTo(result.getDailyRevenues().get(0).getRevenue()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getDailyRevenues().get(1).getRevenue()));
        assertEquals(0, BigDecimal.valueOf(60000).compareTo(result.getDailyRevenues().get(2).getRevenue()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getDailyRevenues().get(3).getRevenue()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getDailyRevenues().get(4).getRevenue()));
        assertEquals(0, BigDecimal.valueOf(180000).compareTo(result.getDailyRevenues().get(5).getRevenue()));

        // Average = 460000 / 6 = 76666.67
        assertEquals(0, BigDecimal.valueOf(76666.67).compareTo(result.getAverageRevenuePerDay()));
    }

    @Test
    void getReport_ThrowsInvalidReportDateRangeException_WhenStartDateAfterEndDate() {
        // Act & Assert
        assertThrows(InvalidReportDateRangeException.class, () ->
                getRevenueReportUseCase.getReport(endDate, startDate));
    }

    @Test
    void getReport_Success_DefaultDates() {
        // Arrange
        LocalDate defaultStart = LocalDate.now().withDayOfMonth(1);
        LocalDate defaultEnd = LocalDate.now();

        when(invoiceRepositoryPort.findByDateRangeAndStatus(defaultStart, defaultEnd, InvoiceStatus.PAID))
                .thenReturn(Collections.emptyList());

        when(reportMapper.toDto(any(RevenueReport.class))).thenAnswer(invocation -> {
            RevenueReport report = invocation.getArgument(0);
            List<DailyRevenueDto> dailyDtos = report.getDailyRevenues().stream()
                    .map(d -> new DailyRevenueDto(d.getDate(), d.getRevenue()))
                    .toList();
            return new RevenueReportDto(
                    report.getStartDate(),
                    report.getEndDate(),
                    report.getTotalRevenue(),
                    report.getClinicalExamRevenue(),
                    report.getLabTestRevenue(),
                    report.getMedicineRevenue(),
                    dailyDtos,
                    report.getAverageRevenuePerDay()
            );
        });

        // Act
        RevenueReportDto result = getRevenueReportUseCase.getReport(null, null);

        // Assert
        assertNotNull(result);
        assertEquals(defaultStart, result.getStartDate());
        assertEquals(defaultEnd, result.getEndDate());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalRevenue()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getAverageRevenuePerDay()));
    }
}
