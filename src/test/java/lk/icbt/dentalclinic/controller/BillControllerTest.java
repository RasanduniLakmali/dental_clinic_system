package lk.icbt.dentalclinic.controller;

import lk.icbt.dentalclinic.dao.AppointmentDao;
import lk.icbt.dentalclinic.dao.BillDao;
import lk.icbt.dentalclinic.dao.TreatmentDao;
import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.AppointmentStatus;
import lk.icbt.dentalclinic.model.Bill;
import lk.icbt.dentalclinic.model.Treatment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillControllerTest {

    @Mock
    private BillDao bills;

    @Mock
    private AppointmentDao appointments;

    @Mock
    private TreatmentDao treatments;

    private BillController controller;

    @BeforeEach
    void setUp() {
        controller = new BillController(bills, appointments, treatments);
    }

    private Appointment createAppointment(String number, String treatmentType) {
        Appointment appointment = new Appointment();

        appointment.setAppointmentNumber(number);
        appointment.setPatientName("Kamal Perera");
        appointment.setAddress("25 Main Street, Ratnapura");
        appointment.setContactNumber("0771234567");
        appointment.setDentistName("Dr. Fernando");
        appointment.setTreatmentType(treatmentType);
        appointment.setAppointmentDate(LocalDate.of(2026, 9, 10));
        appointment.setAppointmentTime(LocalTime.of(10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointment;
    }

    private Treatment createTreatment(String name, String fee) {
        Treatment treatment = new Treatment();

        treatment.setName(name);
        treatment.setFee(new BigDecimal(fee));

        return treatment;
    }

    private Bill createBill(
            String appointmentNumber,
            String consultationFee,
            String treatmentFee,
            String totalAmount) {

        Bill bill = new Bill();

        bill.setAppointmentNumber(appointmentNumber);
        bill.setConsultationFee(new BigDecimal(consultationFee));
        bill.setTreatmentFee(new BigDecimal(treatmentFee));
        bill.setTotalAmount(new BigDecimal(totalAmount));
        bill.setGeneratedAt(LocalDateTime.now());

        return bill;
    }

    @Test
    void TC_BILL_001_generateBillForScaling() {

        Appointment appointment =
                createAppointment("A1001", "Scaling");

        Treatment treatment =
                createTreatment("Scaling", "2500.00");

        Bill savedBill =
                createBill("A1001", "1000.00", "2500.00", "3500.00");

        when(bills.findByAppointmentNumber("A1001"))
                .thenReturn(Optional.empty());

        when(appointments.findById("A1001"))
                .thenReturn(Optional.of(appointment));

        when(treatments.findByNameIgnoreCase("Scaling"))
                .thenReturn(Optional.of(treatment));

        when(bills.save(any(Bill.class)))
                .thenReturn(savedBill);

        ResponseEntity<?> response =
                controller.create("A1001");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(bills).save(any(Bill.class));
    }

    @Test
    void TC_BILL_002_generateCorrectTotalForFilling() {

        Appointment appointment =
                createAppointment("A1002", "Filling");

        Treatment treatment =
                createTreatment("Filling", "4000.00");

        Bill savedBill =
                createBill("A1002", "1000.00", "4000.00", "5000.00");

        when(bills.findByAppointmentNumber("A1002"))
                .thenReturn(Optional.empty());

        when(appointments.findById("A1002"))
                .thenReturn(Optional.of(appointment));

        when(treatments.findByNameIgnoreCase("Filling"))
                .thenReturn(Optional.of(treatment));

        when(bills.save(any(Bill.class)))
                .thenReturn(savedBill);

        ResponseEntity<?> response =
                controller.create("A1002");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Bill saved = captureSavedBill();

        assertEquals(
                new BigDecimal("5000.00"),
                saved.getTotalAmount()
        );
    }

    @Test
    void TC_BILL_003_generateCorrectTotalForExtraction() {

        Appointment appointment =
                createAppointment("A1003", "Extraction");

        Treatment treatment =
                createTreatment("Extraction", "3500.00");

        Bill savedBill =
                createBill("A1003", "1000.00", "3500.00", "4500.00");

        when(bills.findByAppointmentNumber("A1003"))
                .thenReturn(Optional.empty());

        when(appointments.findById("A1003"))
                .thenReturn(Optional.of(appointment));

        when(treatments.findByNameIgnoreCase("Extraction"))
                .thenReturn(Optional.of(treatment));

        when(bills.save(any(Bill.class)))
                .thenReturn(savedBill);

        ResponseEntity<?> response =
                controller.create("A1003");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Bill saved = captureSavedBill();

        assertEquals(
                new BigDecimal("4500.00"),
                saved.getTotalAmount()
        );
    }

    @Test
    void TC_BILL_004_generateCorrectTotalForRootCanal() {

        Appointment appointment =
                createAppointment("A1004", "Root Canal");

        Treatment treatment =
                createTreatment("Root Canal", "12000.00");

        Bill savedBill =
                createBill("A1004", "1000.00", "12000.00", "13000.00");

        when(bills.findByAppointmentNumber("A1004"))
                .thenReturn(Optional.empty());

        when(appointments.findById("A1004"))
                .thenReturn(Optional.of(appointment));

        when(treatments.findByNameIgnoreCase("Root Canal"))
                .thenReturn(Optional.of(treatment));

        when(bills.save(any(Bill.class)))
                .thenReturn(savedBill);

        ResponseEntity<?> response =
                controller.create("A1004");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Bill saved = captureSavedBill();

        assertEquals(
                new BigDecimal("13000.00"),
                saved.getTotalAmount()
        );
    }

    @Test
    void TC_BILL_005_returnNotFoundWhenAppointmentDoesNotExist() {

        when(bills.findByAppointmentNumber("A9999"))
                .thenReturn(Optional.empty());

        when(appointments.findById("A9999"))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response =
                controller.create("A9999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(bills, never()).save(any(Bill.class));
        verify(treatments, never()).findByNameIgnoreCase(anyString());
    }

    @Test
    void TC_BILL_006_returnBadRequestWhenTreatmentDoesNotExist() {

        Appointment appointment =
                createAppointment("A1005", "Unknown Treatment");

        when(bills.findByAppointmentNumber("A1005"))
                .thenReturn(Optional.empty());

        when(appointments.findById("A1005"))
                .thenReturn(Optional.of(appointment));

        when(treatments.findByNameIgnoreCase("Unknown Treatment"))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response =
                controller.create("A1005");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(bills, never()).save(any(Bill.class));
    }

    @Test
    void TC_BILL_007_returnExistingBillWhenDuplicateBillIsRequested() {

        Bill existingBill =
                createBill(
                        "A1001",
                        "1000.00",
                        "2500.00",
                        "3500.00"
                );

        when(bills.findByAppointmentNumber("A1001"))
                .thenReturn(Optional.of(existingBill));

        ResponseEntity<?> response =
                controller.create("A1001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(appointments, never()).findById(anyString());
        verify(treatments, never()).findByNameIgnoreCase(anyString());
        verify(bills, never()).save(any(Bill.class));
    }

    @Test
    void TC_BILL_008_consultationFeeMustBe1000() {

        Appointment appointment =
                createAppointment("A1006", "Scaling");

        Treatment treatment =
                createTreatment("Scaling", "2500.00");

        Bill savedBill =
                createBill("A1006", "1000.00", "2500.00", "3500.00");

        when(bills.findByAppointmentNumber("A1006"))
                .thenReturn(Optional.empty());

        when(appointments.findById("A1006"))
                .thenReturn(Optional.of(appointment));

        when(treatments.findByNameIgnoreCase("Scaling"))
                .thenReturn(Optional.of(treatment));

        when(bills.save(any(Bill.class)))
                .thenReturn(savedBill);

        controller.create("A1006");

        Bill saved = captureSavedBill();

        assertEquals(
                new BigDecimal("1000.00"),
                saved.getConsultationFee()
        );
    }

    @Test
    void TC_BILL_009_treatmentFeeMustMatchTreatmentPrice() {

        Appointment appointment =
                createAppointment("A1007", "Scaling");

        Treatment treatment =
                createTreatment("Scaling", "2500.00");

        Bill savedBill =
                createBill("A1007", "1000.00", "2500.00", "3500.00");

        when(bills.findByAppointmentNumber("A1007"))
                .thenReturn(Optional.empty());

        when(appointments.findById("A1007"))
                .thenReturn(Optional.of(appointment));

        when(treatments.findByNameIgnoreCase("Scaling"))
                .thenReturn(Optional.of(treatment));

        when(bills.save(any(Bill.class)))
                .thenReturn(savedBill);

        controller.create("A1007");

        Bill saved = captureSavedBill();

        assertEquals(
                treatment.getFee(),
                saved.getTreatmentFee()
        );
    }

    @Test
    void TC_BILL_010_generatePdfForExistingBill() {

        Bill bill =
                createBill(
                        "A1001",
                        "1000.00",
                        "2500.00",
                        "3500.00"
                );

        when(bills.findByAppointmentNumber("A1001"))
                .thenReturn(Optional.of(bill));

        ResponseEntity<byte[]> response =
                controller.generatePdf("A1001");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);

        assertEquals(
                MediaType.APPLICATION_PDF,
                response.getHeaders().getContentType()
        );

        assertTrue(
                response.getHeaders()
                        .getFirst("Content-Disposition")
                        .contains("bill-A1001.pdf")
        );
    }

    @Test
    void TC_BILL_011_returnNotFoundForPdfOfNonExistingBill() {

        when(bills.findByAppointmentNumber("A9999"))
                .thenReturn(Optional.empty());

        ResponseEntity<byte[]> response =
                controller.generatePdf("A9999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertNull(response.getBody());
    }

    @Test
    void TC_BILL_012_savedBillMustContainCorrectAppointmentNumber() {

        Appointment appointment =
                createAppointment("A1008", "Scaling");

        Treatment treatment =
                createTreatment("Scaling", "2500.00");

        Bill savedBill =
                createBill("A1008", "1000.00", "2500.00", "3500.00");

        when(bills.findByAppointmentNumber("A1008"))
                .thenReturn(Optional.empty());

        when(appointments.findById("A1008"))
                .thenReturn(Optional.of(appointment));

        when(treatments.findByNameIgnoreCase("Scaling"))
                .thenReturn(Optional.of(treatment));

        when(bills.save(any(Bill.class)))
                .thenReturn(savedBill);

        controller.create("A1008");

        Bill saved = captureSavedBill();

        assertEquals(
                "A1008",
                saved.getAppointmentNumber()
        );
    }

    private Bill captureSavedBill() {

        ArgumentCaptor<Bill> captor =
                ArgumentCaptor.forClass(Bill.class);

        verify(bills).save(captor.capture());

        return captor.getValue();
    }
}