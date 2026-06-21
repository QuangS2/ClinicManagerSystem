package com.clinicmanager.presentation.advice;

import com.clinicmanager.domain.exception.BusinessException;
import com.clinicmanager.domain.exception.patient.PatientAlreadyExistsException;
import com.clinicmanager.domain.exception.patient.PatientNotFoundException;
import com.clinicmanager.domain.exception.service.MedicalServiceAlreadyExistsException;
import com.clinicmanager.domain.exception.service.MedicalServiceNotFoundException;
import com.clinicmanager.domain.exception.medicine.MedicineAlreadyExistsException;
import com.clinicmanager.domain.exception.medicine.MedicineNotFoundException;
import com.clinicmanager.domain.exception.user.UserAlreadyExistsException;
import com.clinicmanager.domain.exception.user.UserNotFoundException;
import com.clinicmanager.domain.exception.appointment.AppointmentNotFoundException;
import com.clinicmanager.domain.exception.admission.MedicalSlipNotFoundException;
import com.clinicmanager.domain.exception.admission.PatientAlreadyRegisteredException;
import com.clinicmanager.domain.exception.examination.LabTestNotFoundException;
import com.clinicmanager.presentation.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handlePatientNotFoundException(PatientNotFoundException ex) {
        return ApiResponse.error("PATIENT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleAppointmentNotFoundException(AppointmentNotFoundException ex) {
        return ApiResponse.error("APPOINTMENT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(MedicalSlipNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleMedicalSlipNotFoundException(MedicalSlipNotFoundException ex) {
        return ApiResponse.error("SLIP_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(LabTestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleLabTestNotFoundException(LabTestNotFoundException ex) {
        return ApiResponse.error("LAB_TEST_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(PatientAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handlePatientAlreadyRegisteredException(PatientAlreadyRegisteredException ex) {
        return ApiResponse.error("PATIENT_ALREADY_REGISTERED", ex.getMessage());
    }

    @ExceptionHandler(PatientAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handlePatientAlreadyExistsException(PatientAlreadyExistsException ex) {
        return ApiResponse.error("PATIENT_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(MedicalServiceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleMedicalServiceNotFoundException(MedicalServiceNotFoundException ex) {
        return ApiResponse.error("SERVICE_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(MedicalServiceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleMedicalServiceAlreadyExistsException(MedicalServiceAlreadyExistsException ex) {
        return ApiResponse.error("SERVICE_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(MedicineNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleMedicineNotFoundException(MedicineNotFoundException ex) {
        return ApiResponse.error("MEDICINE_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(MedicineAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleMedicineAlreadyExistsException(MedicineAlreadyExistsException ex) {
        return ApiResponse.error("MEDICINE_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleUserNotFoundException(UserNotFoundException ex) {
        return ApiResponse.error("USER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ApiResponse.error("USER_ALREADY_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        return ApiResponse.error("BUSINESS_ERROR", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return ApiResponse.error("VALIDATION_ERROR", errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneralException(Exception ex) {
        return ApiResponse.error("INTERNAL_SERVER_ERROR", "Đã xảy ra lỗi hệ thống: " + ex.getMessage());
    }
}
