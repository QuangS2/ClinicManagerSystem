package com.clinicmanager.application.usecase.admission;

import com.clinicmanager.application.dto.admission.MedicalSlipDto;
import com.clinicmanager.application.dto.admission.RegisterAdmissionRequest;
import com.clinicmanager.application.mapper.admission.MedicalSlipMapper;
import com.clinicmanager.application.port.input.admission.RegisterAdmissionUseCase;
import com.clinicmanager.application.port.output.admission.MedicalSlipRepositoryPort;
import com.clinicmanager.application.port.output.appointment.AppointmentRepositoryPort;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.domain.exception.admission.InvalidMedicalSlipDataException;
import com.clinicmanager.domain.exception.admission.PatientAlreadyRegisteredException;
import com.clinicmanager.domain.exception.appointment.AppointmentNotFoundException;
import com.clinicmanager.domain.exception.patient.PatientNotFoundException;
import com.clinicmanager.domain.model.admission.MedicalSlip;
import com.clinicmanager.domain.model.admission.MedicalSlipStatus;
import com.clinicmanager.domain.model.appointment.Appointment;
import com.clinicmanager.domain.model.appointment.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegisterAdmissionUseCaseImpl implements RegisterAdmissionUseCase {

    private final MedicalSlipRepositoryPort medicalSlipRepositoryPort;
    private final AppointmentRepositoryPort appointmentRepositoryPort;
    private final PatientRepositoryPort patientRepositoryPort;
    private final MedicalSlipMapper medicalSlipMapper;

    @Override
    @Transactional
    public MedicalSlipDto register(RegisterAdmissionRequest request) {
        if (request.getPatientId() == null) {
            throw new InvalidMedicalSlipDataException("Mã bệnh nhân không được để trống.");
        }

        // Check if patient already registered today
        if (medicalSlipRepositoryPort.existsActiveSlipByPatientIdAndDate(request.getPatientId(), LocalDate.now())) {
            throw new PatientAlreadyRegisteredException("Bệnh nhân đã đăng ký khám trong ngày hôm nay.");
        }

        if (request.getAppointmentId() != null) {
            // Flow A: Booked Appointment
            Appointment appointment = appointmentRepositoryPort.findById(request.getAppointmentId())
                    .orElseThrow(() -> new AppointmentNotFoundException("Lịch hẹn không tồn tại."));

            if (!appointment.getPatientId().equals(request.getPatientId())) {
                throw new InvalidMedicalSlipDataException("Mã bệnh nhân không khớp với lịch hẹn.");
            }

            if (!appointment.getAppointmentDate().equals(LocalDate.now())) {
                throw new InvalidMedicalSlipDataException("Lịch hẹn không phải là ngày hôm nay.");
            }

            if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
                throw new InvalidMedicalSlipDataException("Lịch hẹn đã hoàn thành hoặc đã bị hủy.");
            }

            // Update Appointment Status to COMPLETED
            Appointment updatedAppointment = new Appointment(
                    appointment.getId(),
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    AppointmentStatus.COMPLETED,
                    appointment.getPatientId(),
                    appointment.getServiceId()
            );
            appointmentRepositoryPort.save(updatedAppointment);

        } else {
            // Flow B: Walk-in Patient
            if (!patientRepositoryPort.findById(request.getPatientId()).isPresent()) {
                throw new PatientNotFoundException("Bệnh nhân không tồn tại.");
            }
        }

        // Create Medical Slip
        MedicalSlip medicalSlip = new MedicalSlip(
                null,
                LocalDate.now(),
                MedicalSlipStatus.WAITING,
                request.getPatientId(),
                null, null, null, null, null, null
        );

        MedicalSlip savedSlip = medicalSlipRepositoryPort.save(medicalSlip);
        return medicalSlipMapper.toDto(savedSlip);
    }
}
