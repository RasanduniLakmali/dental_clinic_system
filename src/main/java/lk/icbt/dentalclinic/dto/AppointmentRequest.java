package lk.icbt.dentalclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Contact number is required")
    @Pattern(
        regexp = "\\d{10}",
        message = "Contact number must be exactly 10 digits"
    )
    private String contactNumber;

    @NotBlank(message = "Dentist name is required")
    private String dentistName;

    @NotBlank(message = "Treatment type is required")
    private String treatmentType;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    // No-argument constructor
    public AppointmentRequest() {
    }

    // Getters and Setters

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
}