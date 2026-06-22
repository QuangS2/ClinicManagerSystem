package com.clinicmanager.presentation.controller.report;

import com.clinicmanager.application.dto.report.MedicalExaminationCountReportDto;
import com.clinicmanager.application.dto.report.MedicalExaminationRatioReportDto;
import com.clinicmanager.application.dto.report.DiseaseTrendReportDto;
import com.clinicmanager.application.port.input.report.GetMedicalExaminationCountReportUseCase;
import com.clinicmanager.application.port.input.report.GetMedicalExaminationRatioReportUseCase;
import com.clinicmanager.application.port.input.report.GetDiseaseTrendReportUseCase;
import com.clinicmanager.presentation.response.ApiResponse;
import com.clinicmanager.presentation.response.report.DailyExaminationCountResponse;
import com.clinicmanager.presentation.response.report.DailyExaminationRatioResponse;
import com.clinicmanager.presentation.response.report.DiseaseTrendResponse;
import com.clinicmanager.presentation.response.report.MedicalExaminationCountReportResponse;
import com.clinicmanager.presentation.response.report.MedicalExaminationRatioReportResponse;
import com.clinicmanager.presentation.response.report.DiseaseTrendReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final GetMedicalExaminationCountReportUseCase getMedicalExaminationCountReportUseCase;
    private final com.clinicmanager.application.port.input.report.GetRevenueReportUseCase getRevenueReportUseCase;
    private final GetMedicalExaminationRatioReportUseCase getMedicalExaminationRatioReportUseCase;
    private final GetDiseaseTrendReportUseCase getDiseaseTrendReportUseCase;

    @GetMapping("/examination-count")
    @PreAuthorize("hasRole('QUAN_LY')")
    public ApiResponse<MedicalExaminationCountReportResponse> getExaminationCountReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        MedicalExaminationCountReportDto dto = getMedicalExaminationCountReportUseCase.getReport(startDate, endDate);

        List<DailyExaminationCountResponse> dailyResponses = dto.getDailyCounts().stream()
                .map(d -> DailyExaminationCountResponse.builder()
                        .date(d.getDate())
                        .count(d.getCount())
                        .build())
                .toList();

        MedicalExaminationCountReportResponse response = MedicalExaminationCountReportResponse.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .dailyCounts(dailyResponses)
                .totalExaminations(dto.getTotalExaminations())
                .statusBreakdown(dto.getStatusBreakdown())
                .averagePerDay(dto.getAveragePerDay())
                .build();

        return ApiResponse.success(response, "Lấy báo cáo số lượt khám thành công.");
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('QUAN_LY')")
    public ApiResponse<com.clinicmanager.presentation.response.report.RevenueReportResponse> getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        com.clinicmanager.application.dto.report.RevenueReportDto dto = getRevenueReportUseCase.getReport(startDate, endDate);

        List<com.clinicmanager.presentation.response.report.DailyRevenueResponse> dailyResponses = dto.getDailyRevenues().stream()
                .map(d -> com.clinicmanager.presentation.response.report.DailyRevenueResponse.builder()
                        .date(d.getDate())
                        .revenue(d.getRevenue())
                        .build())
                .toList();

        com.clinicmanager.presentation.response.report.RevenueReportResponse response = com.clinicmanager.presentation.response.report.RevenueReportResponse.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .totalRevenue(dto.getTotalRevenue())
                .clinicalExamRevenue(dto.getClinicalExamRevenue())
                .labTestRevenue(dto.getLabTestRevenue())
                .medicineRevenue(dto.getMedicineRevenue())
                .dailyRevenues(dailyResponses)
                .averageRevenuePerDay(dto.getAverageRevenuePerDay())
                .build();

        return ApiResponse.success(response, "Lấy báo cáo doanh thu thành công.");
    }

    @GetMapping("/examination-ratio")
    @PreAuthorize("hasRole('QUAN_LY')")
    public ApiResponse<MedicalExaminationRatioReportResponse> getExaminationRatioReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        MedicalExaminationRatioReportDto dto = getMedicalExaminationRatioReportUseCase.getReport(startDate, endDate);

        List<DailyExaminationRatioResponse> dailyResponses = dto.getDailyRatios().stream()
                .map(d -> DailyExaminationRatioResponse.builder()
                        .date(d.getDate())
                        .totalExaminations(d.getTotalExaminations())
                        .examinationsWithLabTests(d.getExaminationsWithLabTests())
                        .examinationsWithMedicines(d.getExaminationsWithMedicines())
                        .labTestRatio(d.getLabTestRatio())
                        .medicineRatio(d.getMedicineRatio())
                        .build())
                .toList();

        MedicalExaminationRatioReportResponse response = MedicalExaminationRatioReportResponse.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .totalExaminations(dto.getTotalExaminations())
                .totalExaminationsWithLabTests(dto.getTotalExaminationsWithLabTests())
                .totalExaminationsWithMedicines(dto.getTotalExaminationsWithMedicines())
                .overallLabTestRatio(dto.getOverallLabTestRatio())
                .overallMedicineRatio(dto.getOverallMedicineRatio())
                .dailyRatios(dailyResponses)
                .build();

        return ApiResponse.success(response, "Lấy báo cáo tỷ lệ khám bệnh thành công.");
    }

    @GetMapping("/disease-trend")
    @PreAuthorize("hasRole('QUAN_LY')")
    public ApiResponse<DiseaseTrendReportResponse> getDiseaseTrendReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        DiseaseTrendReportDto dto = getDiseaseTrendReportUseCase.getReport(startDate, endDate);

        List<DiseaseTrendResponse> trendResponses = dto.getTrends().stream()
                .map(t -> DiseaseTrendResponse.builder()
                        .diseaseName(t.getDiseaseName())
                        .caseCount(t.getCaseCount())
                        .percentage(t.getPercentage())
                        .build())
                .toList();

        DiseaseTrendReportResponse response = DiseaseTrendReportResponse.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .totalCases(dto.getTotalCases())
                .trends(trendResponses)
                .build();

        return ApiResponse.success(response, "Lấy báo cáo xu hướng mắc bệnh thành công.");
    }
}
