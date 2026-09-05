package lk.icbt.dentalclinic.controller;
import jakarta.validation.Valid;
import lk.icbt.dentalclinic.dao.AppointmentDao;
import lk.icbt.dentalclinic.dto.*;
import lk.icbt.dentalclinic.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/appointments")
public class AppointmentController {
 private final AppointmentDao appointmentDao;
 public AppointmentController(AppointmentDao appointmentDao){this.appointmentDao=appointmentDao;}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
 public ResponseEntity<AppointmentResponse> register(@Valid @RequestBody AppointmentRequest r){
  Appointment a=new Appointment(); a.setAppointmentNumber(nextNumber()); a.setPatientName(r.getPatientName()); a.setAddress(r.getAddress()); a.setContactNumber(r.getContactNumber()); a.setDentistName(r.getDentistName()); a.setTreatmentType(r.getTreatmentType()); a.setAppointmentDate(r.getAppointmentDate()); a.setAppointmentTime(r.getAppointmentTime()); a.setStatus(AppointmentStatus.SCHEDULED);
  return ResponseEntity.status(201).body(AppointmentResponse.from(appointmentDao.save(a)));
 }
 @GetMapping("/{appointmentNumber}") @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DENTIST')") public ResponseEntity<AppointmentResponse> findByNumber(@PathVariable String appointmentNumber){return appointmentDao.findById(appointmentNumber).map(a->ResponseEntity.ok(AppointmentResponse.from(a))).orElseGet(()->ResponseEntity.notFound().build());}
 @GetMapping @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')") public List<AppointmentResponse> findAll(){return appointmentDao.findAll().stream().map(AppointmentResponse::from).toList();}
 @GetMapping("/dentist/{dentistName}") @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DENTIST')") public List<AppointmentResponse> findByDentist(@PathVariable String dentistName){return appointmentDao.findByDentistNameIgnoreCase(dentistName).stream().map(AppointmentResponse::from).toList();}
 private String nextNumber(){ long max=appointmentDao.findAll().stream().map(Appointment::getAppointmentNumber).filter(s->s!=null&&s.matches("A\\d+")).mapToLong(s->Long.parseLong(s.substring(1))).max().orElse(1000); return "A"+(max+1); }
}
