package lk.icbt.dentalclinic.controller;

import lk.icbt.dentalclinic.dao.UserDao;
import lk.icbt.dentalclinic.dto.LoginRequest;
import lk.icbt.dentalclinic.dto.LoginResponse;
import lk.icbt.dentalclinic.model.Role;
import lk.icbt.dentalclinic.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthController createController() {
        return new AuthController(userDao, passwordEncoder);
    }

    private User createUser(
            String username,
            String passwordHash,
            Role role,
            String displayName,
            boolean active) {

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setDisplayName(displayName);
        user.setActive(active);

        return user;
    }

    private LoginRequest createLoginRequest(
            String username,
            String password) {

        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        return request;
    }

    // TC-AUTH-001
    @Test
    void TC_AUTH_001_loginWithValidCredentials() {

        User user = createUser(
                "admin",
                "hashedPassword",
                Role.ADMIN,
                "System Admin",
                true
        );

        when(userDao.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "admin123",
                "hashedPassword"))
                .thenReturn(true);

        LoginRequest request =
                createLoginRequest("admin", "admin123");

        ResponseEntity<LoginResponse> response =
                createController().login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals("admin",
                response.getBody().getUsername());

        assertEquals(Role.ADMIN,
                response.getBody().getRole());

        assertEquals("System Admin",
                response.getBody().getDisplayName());

        assertEquals("Login successful",
                response.getBody().getMessage());

        verify(userDao).findByUsername("admin");

        verify(passwordEncoder).matches(
                "admin123",
                "hashedPassword");
    }

    // TC-AUTH-002
    @Test
    void TC_AUTH_002_loginWithWrongPassword() {

        User user = createUser(
                "admin",
                "hashedPassword",
                Role.ADMIN,
                "System Admin",
                true
        );

        when(userDao.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong123",
                "hashedPassword"))
                .thenReturn(false);

        LoginRequest request =
                createLoginRequest("admin", "wrong123");

        ResponseEntity<LoginResponse> response =
                createController().login(request);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "Incorrect username or password",
                response.getBody().getMessage()
        );

        verify(userDao).findByUsername("admin");

        verify(passwordEncoder).matches(
                "wrong123",
                "hashedPassword");
    }

    // TC-AUTH-003
    @Test
    void TC_AUTH_003_loginWithUnknownUsername() {

        when(userDao.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        LoginRequest request =
                createLoginRequest("unknown", "password123");

        ResponseEntity<LoginResponse> response =
                createController().login(request);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "Incorrect username or password",
                response.getBody().getMessage()
        );

        verify(userDao).findByUsername("unknown");

        // Password should not be checked because the user does not exist.
        verify(passwordEncoder, never())
                .matches(anyString(), anyString());
    }

    // TC-AUTH-004
    @Test
    void TC_AUTH_004_loginWithInactiveUser() {

        User user = createUser(
                "admin",
                "hashedPassword",
                Role.ADMIN,
                "System Admin",
                false
        );

        when(userDao.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        LoginRequest request =
                createLoginRequest("admin", "admin123");

        ResponseEntity<LoginResponse> response =
                createController().login(request);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "Incorrect username or password",
                response.getBody().getMessage()
        );

        verify(userDao).findByUsername("admin");

        // Password should not be checked for an inactive account.
        verify(passwordEncoder, never())
                .matches(anyString(), anyString());
    }

    // TC-AUTH-005
    @Test
    void TC_AUTH_005_validLoginReturnsCorrectRole() {

        User user = createUser(
                "dfernando",
                "hashedPassword",
                Role.DENTIST,
                "Dr. Fernando",
                true
        );

        when(userDao.findByUsername("dfernando"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "dentist123",
                "hashedPassword"))
                .thenReturn(true);

        LoginRequest request =
                createLoginRequest(
                        "dfernando",
                        "dentist123"
                );

        ResponseEntity<LoginResponse> response =
                createController().login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(
                Role.DENTIST,
                response.getBody().getRole()
        );
    }

    // TC-AUTH-006
    @Test
    void TC_AUTH_006_validLoginReturnsCorrectDisplayName() {

        User user = createUser(
                "receptionist",
                "hashedPassword",
                Role.RECEPTIONIST,
                "Priya Perera",
                true
        );

        when(userDao.findByUsername("receptionist"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "reception123",
                "hashedPassword"))
                .thenReturn(true);

        LoginRequest request =
                createLoginRequest(
                        "receptionist",
                        "reception123"
                );

        ResponseEntity<LoginResponse> response =
                createController().login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(
                "Priya Perera",
                response.getBody().getDisplayName()
        );
    }
}