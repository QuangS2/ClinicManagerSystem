package com.clinicmanager.application.usecase.appointment;

import com.clinicmanager.application.dto.appointment.AppointmentDto;
import com.clinicmanager.application.dto.appointment.BookAppointmentRequest;
import com.clinicmanager.application.mapper.appointment.AppointmentMapper;
import com.clinicmanager.application.port.input.appointment.BookAppointmentUseCase;
import com.clinicmanager.application.port.output.appointment.AppointmentRepositoryPort;
import com.clinicmanager.application.port.output.patient.PatientRepositoryPort;
import com.clinicmanager.application.port.output.service.MedicalServiceRepositoryPort;
import com.clinicmanager.domain.exception.patient.PatientNotFoundException;
import com.clinicmanager.domain.exception.service.MedicalServiceNotFoundException;
import com.clinicmanager.domain.model.appointment.Appointment;
import com.clinicmanager.domain.model.appointment.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookAppointmentUseCaseImpl implements BookAppointmentUseCase {

    private final AppointmentRepositoryPort appointmentRepositoryPort;
    private final PatientRepositoryPort patientRepositoryPort;
    private final MedicalServiceRepositoryPort medicalServiceRepositoryPort;
    private final AppointmentMapper appointmentMapper;

    @Override
    @Transactional
    public AppointmentDto book(BookAppointmentRequest request) {
        if (!patientRepositoryPort.findById(request.getPatientId()).isPresent()) {
            throw new PatientNotFoundException("Bệnh nhân không tồn tại.");
        }
        if (!medicalServiceRepositoryPort.findById(request.getServiceId()).isPresent()) {
            throw new MedicalServiceNotFoundException("Dịch vụ khám không tồn tại.");
        }

        Appointment appointment = new Appointment(
                null,
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                AppointmentStatus.PENDING,
                request.getPatientId(),
                request.getServiceId()
        );

        Appointment savedAppointment = appointmentRepositoryPort.save(appointment);
        return appointmentMapper.toDto(savedAppointment);
    }
}
