package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentDao extends JpaRepository<Appointment, String> {
    List<Appointment> findByDentistNameIgnoreCase(String dentistName);
}
