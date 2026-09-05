package lk.icbt.dentalclinic.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String appointmentNumber;

    @Column(nullable = false)
    private BigDecimal consultationFee;

    @Column(nullable = false)
    private BigDecimal treatmentFee;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    // No-argument constructor
    public Bill() {
    }

    // All-argument constructor
    public Bill(Long id,
                String appointmentNumber,
                BigDecimal consultationFee,
                BigDecimal treatmentFee,
                BigDecimal totalAmount,
                LocalDateTime generatedAt) {

        this.id = id;
        this.appointmentNumber = appointmentNumber;
        this.consultationFee = consultationFee;
        this.treatmentFee = treatmentFee;
        this.totalAmount = totalAmount;
        this.generatedAt = generatedAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public BigDecimal getTreatmentFee() {
        return treatmentFee;
    }

    public void setTreatmentFee(BigDecimal treatmentFee) {
        this.treatmentFee = treatmentFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}