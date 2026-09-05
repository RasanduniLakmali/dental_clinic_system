package lk.icbt.dentalclinic.config;

import lk.icbt.dentalclinic.model.Role;
import lk.icbt.dentalclinic.model.Treatment;
import lk.icbt.dentalclinic.model.User;
import lk.icbt.dentalclinic.dao.TreatmentDao;
import lk.icbt.dentalclinic.dao.UserDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds one login per role (Admin, Receptionist, Dentist) and a starter
 * treatment price list so the system is usable immediately after a fresh
 * clone, without needing manual SQL setup.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserDao userDao;
    private final TreatmentDao treatmentDao;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserDao userDao, TreatmentDao treatmentDao,
                       PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.treatmentDao = treatmentDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUser("admin", "admin123", Role.ADMIN, "System Admin", "admin@sunrisedental.local");
        seedUser("receptionist", "reception123", Role.RECEPTIONIST, "Priya Perera", "priya@sunrisedental.local");
        seedUser("dfernando", "dentist123", Role.DENTIST, "Dr. Fernando", "fernando@sunrisedental.local");
        seedUser("djayasuriya", "dentist123", Role.DENTIST, "Dr. Jayasuriya", "jayasuriya@sunrisedental.local");

        seedTreatment("Scaling", "2500.00");
        seedTreatment("Filling", "4000.00");
        seedTreatment("Extraction", "3500.00");
        seedTreatment("Root Canal", "12000.00");
    }

    private void seedUser(String username, String rawPassword, Role role, String displayName, String email) {
        if (userDao.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            user.setDisplayName(displayName);
            user.setEmail(email);
            user.setActive(true);
            userDao.save(user);
        }
    }

    private void seedTreatment(String name, String fee) {
        if (treatmentDao.findByNameIgnoreCase(name).isEmpty()) {
            Treatment treatment = new Treatment();
            treatment.setName(name);
            treatment.setFee(new BigDecimal(fee));
            treatmentDao.save(treatment);
        }
    }
}
