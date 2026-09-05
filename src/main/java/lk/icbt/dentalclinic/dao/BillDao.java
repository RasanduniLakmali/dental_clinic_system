package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillDao extends JpaRepository<Bill, Long> {
    Optional<Bill> findByAppointmentNumber(String appointmentNumber);
}
