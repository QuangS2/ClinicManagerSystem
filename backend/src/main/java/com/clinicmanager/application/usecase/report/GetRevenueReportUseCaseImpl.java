package com.clinicmanager.application.usecase.report;

import com.clinicmanager.application.dto.report.RevenueReportDto;
import com.clinicmanager.application.mapper.report.ReportMapper;
import com.clinicmanager.application.port.input.report.GetRevenueReportUseCase;
import com.clinicmanager.application.port.output.payment.InvoiceRepositoryPort;
import com.clinicmanager.domain.exception.report.InvalidReportDateRangeException;
import com.clinicmanager.domain.model.payment.Invoice;
import com.clinicmanager.domain.model.payment.InvoiceStatus;
import com.clinicmanager.domain.model.report.DailyRevenue;
import com.clinicmanager.domain.model.report.RevenueReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRevenueReportUseCaseImpl implements GetRevenueReportUseCase {

    private final InvoiceRepositoryPort invoiceRepositoryPort;
    private final ReportMapper reportMapper;

    @Override
    @Transactional(readOnly = true)
    public RevenueReportDto getReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidReportDateRangeException("Ngày bắt đầu không được nằm sau ngày kết thúc.");
        }

        // 1. Fetch all PAID invoices in range
        List<Invoice> invoices = invoiceRepositoryPort.findByDateRangeAndStatus(startDate, endDate, InvoiceStatus.PAID);

        // 2. Sum clinical exam, lab test, medicine, and total revenues
        BigDecimal totalClinicalExam = invoices.stream()
                .map(Invoice::getClinicalExamAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLabTest = invoices.stream()
                .map(Invoice::getLabTestAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMedicine = invoices.stream()
                .map(Invoice::getMedicineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOverall = invoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Group by date and sum total amount
        Map<LocalDate, BigDecimal> revenueMap = invoices.stream()
                .collect(Collectors.groupingBy(
                        inv -> inv.getCreatedAt().toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, Invoice::getTotalAmount, BigDecimal::add)
                ));

        // 4. Fill missing dates with zero revenue
        List<DailyRevenue> dailyRevenues = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            BigDecimal revenue = revenueMap.getOrDefault(current, BigDecimal.ZERO);
            dailyRevenues.add(new DailyRevenue(current, revenue));
            current = current.plusDays(1);
        }

        // 5. Calculate average revenue per day
        long daysCount = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal averageRevenuePerDay = daysCount > 0
                ? totalOverall.divide(BigDecimal.valueOf(daysCount), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 6. Create domain object
        RevenueReport report = new RevenueReport(
                startDate,
                endDate,
                totalOverall,
                totalClinicalExam,
                totalLabTest,
                totalMedicine,
                dailyRevenues,
                averageRevenuePerDay
        );

        // 7. Map to DTO and return
        return reportMapper.toDto(report);
    }
}
