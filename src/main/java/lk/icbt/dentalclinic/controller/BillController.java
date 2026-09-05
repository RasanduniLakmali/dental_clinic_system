package lk.icbt.dentalclinic.controller;
import lk.icbt.dentalclinic.dao.*; import lk.icbt.dentalclinic.dto.BillResponse; import lk.icbt.dentalclinic.model.*;
import org.springframework.http.ResponseEntity; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal; import java.time.LocalDateTime;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillDao bills;
    private final AppointmentDao appointments;
    private final TreatmentDao treatments;

    public BillController(
            BillDao bills,
            AppointmentDao appointments,
            TreatmentDao treatments) {

        this.bills = bills;
        this.appointments = appointments;
        this.treatments = treatments;
    }

    @PostMapping("/{number}/generate")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<?> create(@PathVariable String number) {

        if (bills.findByAppointmentNumber(number).isPresent()) {
            return bills.findByAppointmentNumber(number)
                    .map(b -> ResponseEntity.ok(BillResponse.from(b)))
                    .orElseThrow();
        }

        Appointment a = appointments.findById(number).orElse(null);

        if (a == null) {
            return ResponseEntity.notFound().build();
        }

        Treatment t = treatments
                .findByNameIgnoreCase(a.getTreatmentType())
                .orElse(null);

        if (t == null) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of(
                            "message", "Treatment not found"
                    ));
        }

        Bill b = new Bill();

        b.setAppointmentNumber(number);
        b.setConsultationFee(new BigDecimal("1000.00"));
        b.setTreatmentFee(t.getFee());
        b.setTotalAmount(
                b.getConsultationFee().add(b.getTreatmentFee())
        );
        b.setGeneratedAt(LocalDateTime.now());

        return ResponseEntity
                .status(201)
                .body(BillResponse.from(bills.save(b)));
    }
    
    @GetMapping("/{number}/pdf")
@PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
public ResponseEntity<byte[]> generatePdf(@PathVariable String number) {

    Bill bill = bills.findByAppointmentNumber(number).orElse(null);

    if (bill == null) {
        return ResponseEntity.notFound().build();
    }

    try {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        document.add(new Paragraph("SUNRISE DENTAL CLINIC"));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Dental Bill"));
        document.add(new Paragraph("Appointment Number: " + bill.getAppointmentNumber()));
        document.add(new Paragraph("Consultation Fee: Rs. " + bill.getConsultationFee()));
        document.add(new Paragraph("Treatment Fee: Rs. " + bill.getTreatmentFee()));
        document.add(new Paragraph("Total Amount: Rs. " + bill.getTotalAmount()));
        document.add(new Paragraph("Generated At: " + bill.getGeneratedAt()));

        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=bill-" + number + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(outputStream.toByteArray());

    } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}
}
