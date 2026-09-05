package lk.icbt.dentalclinic.controller;

import jakarta.validation.Valid;
import lk.icbt.dentalclinic.dto.LoginRequest;
import lk.icbt.dentalclinic.dto.LoginResponse;
import lk.icbt.dentalclinic.model.User;
import lk.icbt.dentalclinic.dao.UserDao;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Used by the JavaFX login screen to verify credentials up front and learn
 * the user's role, so the client can show the right menu (Admin /
 * Receptionist / Dentist). All subsequent calls still authenticate again
 * via HTTP Basic, since this endpoint issues no session or token.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userDao.findByUsername(request.getUsername()).orElse(null);

        if (user == null || !user.isActive() || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(request.getUsername(), null, null, "Incorrect username or password"));
        }

        return ResponseEntity.ok(new LoginResponse(user.getUsername(), user.getRole(), user.getDisplayName(), "Login successful"));
    }
}
