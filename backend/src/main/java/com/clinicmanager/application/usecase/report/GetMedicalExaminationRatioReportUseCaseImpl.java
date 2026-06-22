package com.clinicmanager.application.usecase.report;

import com.clinicmanager.application.dto.report.MedicalExaminationRatioReportDto;
import com.clinicmanager.application.mapper.report.ReportMapper;
import com.clinicmanager.application.port.input.report.GetMedicalExaminationRatioReportUseCase;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.examination.LabTestRepositoryPort;
import com.clinicmanager.application.port.output.examination.PrescriptionRepositoryPort;
import com.clinicmanager.domain.exception.report.InvalidReportDateRangeException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.examination.LabTest;
import com.clinicmanager.domain.model.examination.Prescription;
import com.clinicmanager.domain.model.report.DailyExaminationRatio;
import com.clinicmanager.domain.model.report.MedicalExaminationRatioReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetMedicalExaminationRatioReportUseCaseImpl implements GetMedicalExaminationRatioReportUseCase {

    private final MedicalSlipRepositoryPort medicalSlipRepositoryPort;
    private final LabTestRepositoryPort labTestRepositoryPort;
    private final PrescriptionRepositoryPort prescriptionRepositoryPort;
    private final ReportMapper reportMapper;

    @Override
    @Transactional(readOnly = true)
    public MedicalExaminationRatioReportDto getReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidReportDateRangeException("Ngày bắt đầu không được nằm sau ngày kết thúc.");
        }

        // 1. Fetch all medical slips in range
        List<MedicalSlip> slips = medicalSlipRepositoryPort.findByDateRange(startDate, endDate);
        List<UUID> slipIds = slips.stream().map(MedicalSlip::getId).toList();

        // 2. Fetch associated lab tests and prescriptions
        List<LabTest> labTests = labTestRepositoryPort.findByMedicalSlipIds(slipIds);
        List<Prescription> prescriptions = prescriptionRepositoryPort.findByMedicalSlipIds(slipIds);

        // 3. Create set of medical slips with lab tests/prescriptions for O(1) lookup
        Set<UUID> slipsWithLabTests = labTests.stream()
                .map(LabTest::getMedicalSlipId)
                .collect(Collectors.toSet());

        Set<UUID> slipsWithMedicines = prescriptions.stream()
                .map(Prescription::getMedicalSlipId)
                .collect(Collectors.toSet());

        // 4. Group slips by date in memory
        Map<LocalDate, List<MedicalSlip>> slipsByDate = slips.stream()
                .collect(Collectors.groupingBy(MedicalSlip::getExaminationDate));

        // 5. Build daily ratios (filling missing dates)
        List<DailyExaminationRatio> dailyRatios = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            List<MedicalSlip> dateSlips = slipsByDate.getOrDefault(current, Collections.emptyList());
            long total = dateSlips.size();
            long labTestCount = 0;
            long medicineCount = 0;

            for (MedicalSlip slip : dateSlips) {
                if (slipsWithLabTests.contains(slip.getId())) {
                    labTestCount++;
                }
                if (slipsWithMedicines.contains(slip.getId())) {
                    medicineCount++;
                }
            }

            double labTestRatio = total > 0 ? (labTestCount * 100.0) / total : 0.0;
            double medicineRatio = total > 0 ? (medicineCount * 100.0) / total : 0.0;

            dailyRatios.add(new DailyExaminationRatio(
                    current,
                    total,
                    labTestCount,
                    medicineCount,
                    labTestRatio,
                    medicineRatio
            ));
            current = current.plusDays(1);
        }

        // 6. Calculate overall ratios
        long totalExaminations = slips.size();
        long totalWithLabTests = slips.stream().filter(s -> slipsWithLabTests.contains(s.getId())).count();
        long totalWithMedicines = slips.stream().filter(s -> slipsWithMedicines.contains(s.getId())).count();

        double overallLabTestRatio = totalExaminations > 0 ? (totalWithLabTests * 100.0) / totalExaminations : 0.0;
        double overallMedicineRatio = totalExaminations > 0 ? (totalWithMedicines * 100.0) / totalExaminations : 0.0;

        // 7. Create report domain object
        MedicalExaminationRatioReport report = new MedicalExaminationRatioReport(
                startDate,
                endDate,
                totalExaminations,
                totalWithLabTests,
                totalWithMedicines,
                overallLabTestRatio,
                overallMedicineRatio,
                dailyRatios
        );

        // 8. Map to DTO and return
        return reportMapper.toDto(report);
    }
}
