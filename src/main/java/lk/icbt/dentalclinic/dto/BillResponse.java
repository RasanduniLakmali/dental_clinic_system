package lk.icbt.dentalclinic.dto;

import lk.icbt.dentalclinic.model.Bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillResponse {

    private String appointmentNumber;
    private BigDecimal consultationFee;
    private BigDecimal treatmentFee;
    private BigDecimal totalAmount;
    private LocalDateTime generatedAt;

    // No-argument constructor
    public BillResponse() {
    }

    // All-argument constructor
    public BillResponse(
            String appointmentNumber,
            BigDecimal consultationFee,
            BigDecimal treatmentFee,
            BigDecimal totalAmount,
            LocalDateTime generatedAt) {

        this.appointmentNumber = appointmentNumber;
        this.consultationFee = consultationFee;
        this.treatmentFee = treatmentFee;
        this.totalAmount = totalAmount;
        this.generatedAt = generatedAt;
    }

    // Getters

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public BigDecimal getTreatmentFee() {
        return treatmentFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    // Static factory method
    public static BillResponse from(Bill b) {
        return new BillResponse(
                b.getAppointmentNumber(),
                b.getConsultationFee(),
                b.getTreatmentFee(),
                b.getTotalAmount(),
                b.getGeneratedAt()
        );
    }
}