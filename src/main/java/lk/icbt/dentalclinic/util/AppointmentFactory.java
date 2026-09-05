package lk.icbt.dentalclinic.util;

import lk.icbt.dentalclinic.dto.AppointmentRequest;
import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.AppointmentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Factory pattern (Task B design pattern requirement).
 *
 * Centralises how an Appointment entity is assembled from an inbound request,
 * including default values (today's date/time when omitted) and the
 * auto-generated appointment number.
 */
@Component
public class AppointmentFactory {

    public Appointment create(AppointmentRequest request) {

        String appointmentNumber =
                AppointmentNumberGenerator.getInstance().next();

        Appointment appointment = new Appointment();

        appointment.setAppointmentNumber(appointmentNumber);
        appointment.setPatientName(request.getPatientName());
        appointment.setAddress(request.getAddress());
        appointment.setContactNumber(request.getContactNumber());
        appointment.setDentistName(request.getDentistName());
        appointment.setTreatmentType(request.getTreatmentType());

        appointment.setAppointmentDate(
                request.getAppointmentDate() != null
                        ? request.getAppointmentDate()
                        : LocalDate.now()
        );

        appointment.setAppointmentTime(
                request.getAppointmentTime() != null
                        ? request.getAppointmentTime()
                        : LocalTime.now()
        );

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointment;
    }
}