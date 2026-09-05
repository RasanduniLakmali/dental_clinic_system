package lk.icbt.dentalclinic.dto;

import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentResponse {

    private String appointmentNumber;
    private String patientName;
    private String address;
    private String contactNumber;
    private String dentistName;
    private String treatmentType;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;

    // No-argument constructor
    public AppointmentResponse() {
    }

    // All-argument constructor
    public AppointmentResponse(
            String appointmentNumber,
            String patientName,
            String address,
            String contactNumber,
            String dentistName,
            String treatmentType,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus status) {

        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistName = dentistName;
        this.treatmentType = treatmentType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Getters

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getDentistName() {
        return dentistName;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    // Static factory method
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getAppointmentNumber(),
                a.getPatientName(),
                a.getAddress(),
                a.getContactNumber(),
                a.getDentistName(),
                a.getTreatmentType(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                a.getStatus()
        );
    }
}