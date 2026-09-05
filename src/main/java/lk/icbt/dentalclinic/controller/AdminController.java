package lk.icbt.dentalclinic.controller;

import jakarta.validation.Valid;
import lk.icbt.dentalclinic.dao.UserDao;
import lk.icbt.dentalclinic.dto.CreateStaffRequest;
import lk.icbt.dentalclinic.dto.StaffResponse;
import lk.icbt.dentalclinic.model.Role;
import lk.icbt.dentalclinic.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/staff")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final boolean mailEnabled;

    public AdminController(UserDao userDao, PasswordEncoder passwordEncoder, JavaMailSender mailSender,
                           @Value("${app.mail.enabled:true}") boolean mailEnabled) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.mailEnabled = mailEnabled;
    }

    @GetMapping
    public List<StaffResponse> list() {
        return userDao.findAll().stream().filter(u -> u.getRole() != Role.ADMIN)
                .map(StaffResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateStaffRequest request) {
        if (request.getRole() == Role.ADMIN) return ResponseEntity.badRequest().body(Map.of("message", "Admin accounts cannot be created here"));
        if (userDao.findByEmailIgnoreCase(request.getEmail()).isPresent()) return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        String username = generateUsername(request.getDisplayName());
        String tempPassword = generatePassword();
        User user = new User();
        user.setUsername(username); user.setDisplayName(request.getDisplayName()); user.setEmail(request.getEmail());
        user.setRole(request.getRole()); user.setPasswordHash(passwordEncoder.encode(tempPassword)); user.setActive(true);
        user = userDao.save(user);
        boolean sent = sendCredentials(user, tempPassword);
        return ResponseEntity.status(201).body(StaffResponse.created(user, sent ? null : tempPassword, sent));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> setActive(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return userDao.findById(id).map(user -> { user.setActive(Boolean.TRUE.equals(body.get("active"))); return ResponseEntity.ok(StaffResponse.from(userDao.save(user))); })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private String generateUsername(String name) {
        String base = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        if (base.isBlank()) base = "staff";
        String username = base; int n = 1;
        while (userDao.findByUsername(username).isPresent()) username = base + (++n);
        return username;
    }
    private String generatePassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$";
        SecureRandom r = new SecureRandom(); StringBuilder b = new StringBuilder();
        for (int i=0;i<10;i++) b.append(chars.charAt(r.nextInt(chars.length()))); return b.toString();
    }
    private boolean sendCredentials(User user, String password) {
        if (!mailEnabled) return false;
        try { SimpleMailMessage m = new SimpleMailMessage(); m.setTo(user.getEmail()); m.setSubject("Sunrise Dental Clinic - Your Login Details");
            m.setText("Hello " + user.getDisplayName() + ",\n\nUsername: " + user.getUsername() + "\nTemporary password: " + password + "\nRole: " + user.getRole() + "\n\nPlease change your password after first login.");
            mailSender.send(m); return true; } catch (Exception e) { return false; }
    }
}
