package com.clinicmanager.application.usecase.report;

import com.clinicmanager.application.dto.report.DiseaseTrendReportDto;
import com.clinicmanager.application.mapper.report.ReportMapper;
import com.clinicmanager.application.port.input.report.GetDiseaseTrendReportUseCase;
import com.clinicmanager.application.port.output.examination.MedicalRecordRepositoryPort;
import com.clinicmanager.domain.exception.report.InvalidReportDateRangeException;
import com.clinicmanager.domain.model.examination.MedicalRecord;
import com.clinicmanager.domain.model.report.DiseaseTrend;
import com.clinicmanager.domain.model.report.DiseaseTrendReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GetDiseaseTrendReportUseCaseImpl implements GetDiseaseTrendReportUseCase {

    private final MedicalRecordRepositoryPort medicalRecordRepositoryPort;
    private final ReportMapper reportMapper;

    @Override
    @Transactional(readOnly = true)
    public DiseaseTrendReportDto getReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidReportDateRangeException("Ngày bắt đầu không được nằm sau ngày kết thúc.");
        }

        List<MedicalRecord> records = medicalRecordRepositoryPort.findByDateRange(startDate, endDate);
        long totalCases = records.size();

        if (totalCases == 0) {
            DiseaseTrendReport report = new DiseaseTrendReport(startDate, endDate, 0, List.of());
            return reportMapper.toDto(report);
        }

        Map<String, Long> countMap = new HashMap<>();
        for (MedicalRecord rec : records) {
            String diag = rec.getDiagnosis();
            if (diag != null) {
                String norm = diag.trim().toLowerCase();
                if (!norm.isEmpty()) {
                    countMap.put(norm, countMap.getOrDefault(norm, 0L) + 1);
                }
            }
        }

        List<DiseaseTrend> trends = new ArrayList<>();
        for (Map.Entry<String, Long> entry : countMap.entrySet()) {
            String key = entry.getKey();
            long count = entry.getValue();
            double percentage = (count * 100.0) / totalCases;

            // Normalize first letter uppercase, others lowercase
            String formattedName = Character.toUpperCase(key.charAt(0)) + key.substring(1);
            trends.add(new DiseaseTrend(formattedName, count, percentage));
        }

        // Sort by caseCount descending, then alphabetically by diseaseName
        trends.sort((t1, t2) -> {
            int countCompare = Long.compare(t2.getCaseCount(), t1.getCaseCount());
            if (countCompare != 0) {
                return countCompare;
            }
            return t1.getDiseaseName().compareTo(t2.getDiseaseName());
        });

        DiseaseTrendReport report = new DiseaseTrendReport(startDate, endDate, totalCases, trends);
        return reportMapper.toDto(report);
    }
}
