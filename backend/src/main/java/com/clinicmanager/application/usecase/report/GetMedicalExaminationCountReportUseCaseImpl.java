package com.clinicmanager.application.usecase.report;

import com.clinicmanager.application.dto.report.MedicalExaminationCountReportDto;
import com.clinicmanager.application.mapper.report.ReportMapper;
import com.clinicmanager.application.port.input.report.GetMedicalExaminationCountReportUseCase;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.domain.exception.report.InvalidReportDateRangeException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.report.DailyExaminationCount;
import com.clinicmanager.domain.model.report.MedicalExaminationCountReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMedicalExaminationCountReportUseCaseImpl implements GetMedicalExaminationCountReportUseCase {

    private final MedicalSlipRepositoryPort medicalSlipRepositoryPort;
    private final ReportMapper reportMapper;

    @Override
    @Transactional(readOnly = true)
    public MedicalExaminationCountReportDto getReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidReportDateRangeException("Ngày bắt đầu không được nằm sau ngày kết thúc.");
        }

        // 1. Fetch all medical slips within the date range
        List<MedicalSlip> slips = medicalSlipRepositoryPort.findByDateRange(startDate, endDate);

        // 2. Count medical slips by date
        Map<LocalDate, Long> countMap = slips.stream()
                .collect(Collectors.groupingBy(
                        MedicalSlip::getExaminationDate,
                        Collectors.counting()
                ));

        // 3. Fill in missing dates in the range with 0 count
        List<DailyExaminationCount> dailyCounts = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            long count = countMap.getOrDefault(current, 0L);
            dailyCounts.add(new DailyExaminationCount(current, count));
            current = current.plusDays(1);
        }

        // 4. Calculate total examinations
        long totalExaminations = slips.size();

        // 5. Count by status
        Map<String, Long> statusBreakdown = slips.stream()
                .collect(Collectors.groupingBy(
                        slip -> slip.getStatus().name(),
                        Collectors.counting()
                ));

        // Ensure all statuses are initialized to 0 if not present
        for (MedicalSlipStatus status : MedicalSlipStatus.values()) {
            statusBreakdown.putIfAbsent(status.name(), 0L);
        }

        // 6. Calculate average examinations per day
        long daysCount = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double averagePerDay = daysCount > 0 ? (double) totalExaminations / daysCount : 0.0;

        // 7. Create report domain object
        MedicalExaminationCountReport report = new MedicalExaminationCountReport(
                startDate,
                endDate,
                dailyCounts,
                totalExaminations,
                statusBreakdown,
                averagePerDay
        );

        // 8. Map and return
        return reportMapper.toDto(report);
    }
}
