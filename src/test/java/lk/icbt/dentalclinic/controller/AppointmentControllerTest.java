package lk.icbt.dentalclinic.controller;

import lk.icbt.dentalclinic.dao.AppointmentDao;
import lk.icbt.dentalclinic.dto.AppointmentRequest;
import lk.icbt.dentalclinic.dto.AppointmentResponse;
import lk.icbt.dentalclinic.model.Appointment;
import lk.icbt.dentalclinic.model.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentDao appointmentDao;

    private AppointmentController controller;

    @BeforeEach
    void setUp() {
        controller = new AppointmentController(appointmentDao);
    }

    private AppointmentRequest createValidRequest() {

        AppointmentRequest request = new AppointmentRequest();

        request.setPatientName("Kamal Perera");
        request.setAddress("25 Main Street, Ratnapura");
        request.setContactNumber("0771234567");
        request.setDentistName("Dr. Fernando");
        request.setTreatmentType("Scaling");
        request.setAppointmentDate(LocalDate.of(2026, 9, 10));
        request.setAppointmentTime(LocalTime.of(10, 0));

        return request;
    }

    private Appointment createAppointment(String number) {

        return new Appointment(
                number,
                "Kamal Perera",
                "25 Main Street, Ratnapura",
                "0771234567",
                "Dr. Fernando",
                "Scaling",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(10, 0),
                AppointmentStatus.SCHEDULED
        );
    }

    // ---------------------------------------------------------
    // TC-APP-001
    // Register a valid appointment
    // ---------------------------------------------------------

    @Test
    void TC_APP_001_registerValidAppointment() {

        AppointmentRequest request = createValidRequest();

        when(appointmentDao.findAll()).thenReturn(List.of());

        when(appointmentDao.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<AppointmentResponse> response =
                controller.register(request);

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "A1001",
                response.getBody().getAppointmentNumber()
        );

        assertEquals(
                "Kamal Perera",
                response.getBody().getPatientName()
        );

        assertEquals(
                "Dr. Fernando",
                response.getBody().getDentistName()
        );

        assertEquals(
                "Scaling",
                response.getBody().getTreatmentType()
        );

        verify(appointmentDao).findAll();
        verify(appointmentDao).save(any(Appointment.class));
    }

    // ---------------------------------------------------------
    // TC-APP-002
    // Newly registered appointment should be SCHEDULED
    // ---------------------------------------------------------

    @Test
    void TC_APP_002_newAppointmentHasScheduledStatus() {

        AppointmentRequest request = createValidRequest();

        when(appointmentDao.findAll()).thenReturn(List.of());

        when(appointmentDao.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<AppointmentResponse> response =
                controller.register(request);

        assertNotNull(response.getBody());

        assertEquals(
                AppointmentStatus.SCHEDULED,
                response.getBody().getStatus()
        );
    }

    // ---------------------------------------------------------
    // TC-APP-003
    // Generate next appointment number
    // ---------------------------------------------------------

    @Test
    void TC_APP_003_generatesNextAppointmentNumber() {

        Appointment existing1 = createAppointment("A1001");
        Appointment existing2 = createAppointment("A1005");
        Appointment existing3 = createAppointment("A1003");

        when(appointmentDao.findAll())
                .thenReturn(List.of(
                        existing1,
                        existing2,
                        existing3
                ));

        when(appointmentDao.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentRequest request = createValidRequest();

        ResponseEntity<AppointmentResponse> response =
                controller.register(request);

        assertNotNull(response.getBody());

        assertEquals(
                "A1006",
                response.getBody().getAppointmentNumber()
        );
    }

    // ---------------------------------------------------------
    // TC-APP-004
    // First appointment should be A1001
    // ---------------------------------------------------------

    @Test
    void TC_APP_004_firstAppointmentStartsAtA1001() {

        when(appointmentDao.findAll()).thenReturn(List.of());

        when(appointmentDao.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentRequest request = createValidRequest();

        ResponseEntity<AppointmentResponse> response =
                controller.register(request);

        assertNotNull(response.getBody());

        assertEquals(
                "A1001",
                response.getBody().getAppointmentNumber()
        );
    }

    // ---------------------------------------------------------
    // TC-APP-005
    // Find existing appointment
    // ---------------------------------------------------------

    @Test
    void TC_APP_005_findExistingAppointment() {

        Appointment appointment = createAppointment("A1001");

        when(appointmentDao.findById("A1001"))
                .thenReturn(Optional.of(appointment));

        ResponseEntity<AppointmentResponse> response =
                controller.findByNumber("A1001");

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "A1001",
                response.getBody().getAppointmentNumber()
        );

        assertEquals(
                "Kamal Perera",
                response.getBody().getPatientName()
        );

        verify(appointmentDao).findById("A1001");
    }

    // ---------------------------------------------------------
    // TC-APP-006
    // Find non-existing appointment
    // ---------------------------------------------------------

    @Test
    void TC_APP_006_findNonExistingAppointment() {

        when(appointmentDao.findById("A9999"))
                .thenReturn(Optional.empty());

        ResponseEntity<AppointmentResponse> response =
                controller.findByNumber("A9999");

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNull(response.getBody());

        verify(appointmentDao).findById("A9999");
    }

    // ---------------------------------------------------------
    // TC-APP-007
    // Retrieve all appointments
    // ---------------------------------------------------------

    @Test
    void TC_APP_007_findAllAppointments() {

        Appointment appointment1 = createAppointment("A1001");
        Appointment appointment2 = createAppointment("A1002");

        when(appointmentDao.findAll())
                .thenReturn(List.of(
                        appointment1,
                        appointment2
                ));

        List<AppointmentResponse> result =
                controller.findAll();

        assertNotNull(result);

        assertEquals(2, result.size());

        assertEquals(
                "A1001",
                result.get(0).getAppointmentNumber()
        );

        assertEquals(
                "A1002",
                result.get(1).getAppointmentNumber()
        );

        verify(appointmentDao).findAll();
    }

    // ---------------------------------------------------------
    // TC-APP-008
    // Find appointments by dentist
    // ---------------------------------------------------------

    @Test
    void TC_APP_008_findAppointmentsByDentist() {

        Appointment appointment1 = createAppointment("A1001");
        Appointment appointment2 = createAppointment("A1002");

        when(appointmentDao.findByDentistNameIgnoreCase(
                "Dr. Fernando"))
                .thenReturn(List.of(
                        appointment1,
                        appointment2
                ));

        List<AppointmentResponse> result =
                controller.findByDentist("Dr. Fernando");

        assertNotNull(result);

        assertEquals(2, result.size());

        assertEquals(
                "Dr. Fernando",
                result.get(0).getDentistName()
        );

        assertEquals(
                "Dr. Fernando",
                result.get(1).getDentistName()
        );

        verify(appointmentDao)
                .findByDentistNameIgnoreCase("Dr. Fernando");
    }

    // ---------------------------------------------------------
    // TC-APP-009
    // Dentist search is case-insensitive at DAO level
    // ---------------------------------------------------------

    @Test
    void TC_APP_009_findAppointmentsByDentistName() {

        Appointment appointment =
                createAppointment("A1001");

        when(appointmentDao.findByDentistNameIgnoreCase(
                "dr. fernando"))
                .thenReturn(List.of(appointment));

        List<AppointmentResponse> result =
                controller.findByDentist("dr. fernando");

        assertEquals(1, result.size());

        assertEquals(
                "Dr. Fernando",
                result.get(0).getDentistName()
        );

        verify(appointmentDao)
                .findByDentistNameIgnoreCase("dr. fernando");
    }

    // ---------------------------------------------------------
    // TC-APP-010
    // Appointment data should be correctly transferred
    // ---------------------------------------------------------

    @Test
    void TC_APP_010_appointmentDataIsMappedCorrectly() {

        AppointmentRequest request = createValidRequest();

        when(appointmentDao.findAll()).thenReturn(List.of());

        when(appointmentDao.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<AppointmentResponse> response =
                controller.register(request);

        assertNotNull(response.getBody());

        AppointmentResponse result =
                response.getBody();

        assertEquals(
                request.getPatientName(),
                result.getPatientName()
        );

        assertEquals(
                request.getAddress(),
                result.getAddress()
        );

        assertEquals(
                request.getContactNumber(),
                result.getContactNumber()
        );

        assertEquals(
                request.getDentistName(),
                result.getDentistName()
        );

        assertEquals(
                request.getTreatmentType(),
                result.getTreatmentType()
        );

        assertEquals(
                request.getAppointmentDate(),
                result.getAppointmentDate()
        );

        assertEquals(
                request.getAppointmentTime(),
                result.getAppointmentTime()
        );
    }

    // ---------------------------------------------------------
    // TC-APP-011
    // Saved appointment should contain generated number
    // ---------------------------------------------------------

    @Test
    void TC_APP_011_savedAppointmentContainsGeneratedNumber() {

        AppointmentRequest request = createValidRequest();

        when(appointmentDao.findAll()).thenReturn(List.of());

        when(appointmentDao.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        controller.register(request);

        ArgumentCaptor<Appointment> captor =
                ArgumentCaptor.forClass(Appointment.class);

        verify(appointmentDao).save(captor.capture());

        Appointment savedAppointment =
                captor.getValue();

        assertEquals(
                "A1001",
                savedAppointment.getAppointmentNumber()
        );
    }

    // ---------------------------------------------------------
    // TC-APP-012
    // Empty appointment list should return empty list
    // ---------------------------------------------------------

    @Test
    void TC_APP_012_findAllWhenNoAppointmentsExist() {

        when(appointmentDao.findAll())
                .thenReturn(List.of());

        List<AppointmentResponse> result =
                controller.findAll();

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(appointmentDao).findAll();
    }
}