package lk.icbt.dentalclinic.dao;

import lk.icbt.dentalclinic.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TreatmentDao extends JpaRepository<Treatment, Long> {
    Optional<Treatment> findByNameIgnoreCase(String name);
}
