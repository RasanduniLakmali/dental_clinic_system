package lk.icbt.dentalclinic.controller;

import lk.icbt.dentalclinic.dao.UserDao;
import lk.icbt.dentalclinic.dto.CreateStaffRequest;
import lk.icbt.dentalclinic.model.Role;
import lk.icbt.dentalclinic.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    private AdminController controller;

    @BeforeEach
    void setUp() {
        // Mail disabled so tests do not actually attempt to send emails.
        controller = new AdminController(
                userDao,
                passwordEncoder,
                mailSender,
                false
        );
    }

    private User createUser(
            Long id,
            String username,
            String displayName,
            String email,
            Role role,
            boolean active) {

        User user = new User();

        user.setId(id);
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setRole(role);
        user.setPasswordHash("hashedPassword");
        user.setActive(active);

        return user;
    }

    private CreateStaffRequest createStaffRequest(
            String displayName,
            String email,
            Role role) {

        CreateStaffRequest request = new CreateStaffRequest();

        request.setDisplayName(displayName);
        request.setEmail(email);
        request.setRole(role);

        return request;
    }

    // ---------------------------------------------------------
    // TC-STAFF-001
    // Create a valid dentist account
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_001_createValidDentist() {

        CreateStaffRequest request =
                createStaffRequest(
                        "Dr. Nimal Perera",
                        "nimal@gmail.com",
                        Role.DENTIST
                );

        when(userDao.findByEmailIgnoreCase("nimal@gmail.com"))
                .thenReturn(Optional.empty());

        when(userDao.findByUsername("drnimalperera"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        User savedUser = createUser(
                1L,
                "drnimalperera",
                "Dr. Nimal Perera",
                "nimal@gmail.com",
                Role.DENTIST,
                true
        );

        when(userDao.save(any(User.class)))
                .thenReturn(savedUser);

        ResponseEntity<?> response =
                controller.create(request);

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        verify(userDao).save(any(User.class));
        verify(passwordEncoder).encode(anyString());
    }

    // ---------------------------------------------------------
    // TC-STAFF-002
    // Create a valid receptionist account
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_002_createValidReceptionist() {

        CreateStaffRequest request =
                createStaffRequest(
                        "Priya Perera",
                        "priya@gmail.com",
                        Role.RECEPTIONIST
                );

        when(userDao.findByEmailIgnoreCase("priya@gmail.com"))
                .thenReturn(Optional.empty());

        when(userDao.findByUsername("priyaperera"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        User savedUser = createUser(
                2L,
                "priyaperera",
                "Priya Perera",
                "priya@gmail.com",
                Role.RECEPTIONIST,
                true
        );

        when(userDao.save(any(User.class)))
                .thenReturn(savedUser);

        ResponseEntity<?> response =
                controller.create(request);

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        verify(userDao).save(any(User.class));
    }

    // ---------------------------------------------------------
    // TC-STAFF-003
    // Admin account creation must be rejected
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_003_rejectAdminAccountCreation() {

        CreateStaffRequest request =
                createStaffRequest(
                        "New Admin",
                        "newadmin@gmail.com",
                        Role.ADMIN
                );

        ResponseEntity<?> response =
                controller.create(request);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        verify(userDao, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    // ---------------------------------------------------------
    // TC-STAFF-004
    // Duplicate email must be rejected
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_004_rejectDuplicateEmail() {

        CreateStaffRequest request =
                createStaffRequest(
                        "Nimal Perera",
                        "existing@gmail.com",
                        Role.DENTIST
                );

        User existingUser = createUser(
                10L,
                "existing",
                "Existing User",
                "existing@gmail.com",
                Role.DENTIST,
                true
        );

        when(userDao.findByEmailIgnoreCase("existing@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        ResponseEntity<?> response =
                controller.create(request);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        verify(userDao, never()).save(any(User.class));
    }

    // ---------------------------------------------------------
    // TC-STAFF-005
    // Username should be generated from display name
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_005_generateUsernameFromDisplayName() {

        CreateStaffRequest request =
                createStaffRequest(
                        "John Perera",
                        "john@gmail.com",
                        Role.DENTIST
                );

        when(userDao.findByEmailIgnoreCase("john@gmail.com"))
                .thenReturn(Optional.empty());

        when(userDao.findByUsername("johnperera"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        User savedUser = createUser(
                3L,
                "johnperera",
                "John Perera",
                "john@gmail.com",
                Role.DENTIST,
                true
        );

        when(userDao.save(any(User.class)))
                .thenReturn(savedUser);

        controller.create(request);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userDao).save(captor.capture());

        User user = captor.getValue();

        assertEquals(
                "johnperera",
                user.getUsername()
        );
    }

    // ---------------------------------------------------------
    // TC-STAFF-006
    // Duplicate username should get a numeric suffix
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_006_generateUniqueUsername() {

        CreateStaffRequest request =
                createStaffRequest(
                        "John Perera",
                        "john2@gmail.com",
                        Role.DENTIST
                );

        when(userDao.findByEmailIgnoreCase("john2@gmail.com"))
                .thenReturn(Optional.empty());

        // First username already exists.
        when(userDao.findByUsername("johnperera"))
                .thenReturn(Optional.of(
                        createUser(
                                4L,
                                "johnperera",
                                "Existing John",
                                "old@gmail.com",
                                Role.DENTIST,
                                true
                        )
                ));

        // Second username is available.
        when(userDao.findByUsername("johnperera2"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        User savedUser = createUser(
                5L,
                "johnperera2",
                "John Perera",
                "john2@gmail.com",
                Role.DENTIST,
                true
        );

        when(userDao.save(any(User.class)))
                .thenReturn(savedUser);

        controller.create(request);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userDao).save(captor.capture());

        assertEquals(
                "johnperera2",
                captor.getValue().getUsername()
        );
    }

    // ---------------------------------------------------------
    // TC-STAFF-007
    // New user should be active
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_007_newStaffIsActive() {

        CreateStaffRequest request =
                createStaffRequest(
                        "Kamal Silva",
                        "kamal@gmail.com",
                        Role.RECEPTIONIST
                );

        when(userDao.findByEmailIgnoreCase("kamal@gmail.com"))
                .thenReturn(Optional.empty());

        when(userDao.findByUsername("kamalsilva"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        User savedUser = createUser(
                6L,
                "kamalsilva",
                "Kamal Silva",
                "kamal@gmail.com",
                Role.RECEPTIONIST,
                true
        );

        when(userDao.save(any(User.class)))
                .thenReturn(savedUser);

        controller.create(request);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userDao).save(captor.capture());

        assertTrue(
                captor.getValue().isActive()
        );
    }

    // ---------------------------------------------------------
    // TC-STAFF-008
    // Password should be encoded before saving
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_008_passwordIsEncoded() {

        CreateStaffRequest request =
                createStaffRequest(
                        "Test Dentist",
                        "test@gmail.com",
                        Role.DENTIST
                );

        when(userDao.findByEmailIgnoreCase("test@gmail.com"))
                .thenReturn(Optional.empty());

        when(userDao.findByUsername("testdentist"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        User savedUser = createUser(
                7L,
                "testdentist",
                "Test Dentist",
                "test@gmail.com",
                Role.DENTIST,
                true
        );

        when(userDao.save(any(User.class)))
                .thenReturn(savedUser);

        controller.create(request);

        verify(passwordEncoder)
                .encode(anyString());

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userDao).save(captor.capture());

        assertEquals(
                "encodedPassword",
                captor.getValue().getPasswordHash()
        );
    }

    // ---------------------------------------------------------
    // TC-STAFF-009
    // Disable an existing staff member
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_009_deactivateStaffMember() {

        User user = createUser(
                10L,
                "johnperera",
                "John Perera",
                "john@gmail.com",
                Role.DENTIST,
                true
        );

        when(userDao.findById(10L))
                .thenReturn(Optional.of(user));

        when(userDao.save(any(User.class)))
                .thenReturn(user);

        ResponseEntity<?> response =
                controller.setActive(
                        10L,
                        Map.of("active", false)
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertFalse(user.isActive());

        verify(userDao).save(user);
    }

    // ---------------------------------------------------------
    // TC-STAFF-010
    // Activate an existing staff member
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_010_activateStaffMember() {

        User user = createUser(
                11L,
                "priyaperera",
                "Priya Perera",
                "priya@gmail.com",
                Role.RECEPTIONIST,
                false
        );

        when(userDao.findById(11L))
                .thenReturn(Optional.of(user));

        when(userDao.save(any(User.class)))
                .thenReturn(user);

        ResponseEntity<?> response =
                controller.setActive(
                        11L,
                        Map.of("active", true)
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertTrue(user.isActive());

        verify(userDao).save(user);
    }

    // ---------------------------------------------------------
    // TC-STAFF-011
    // Non-existing staff status update
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_011_returnNotFoundForUnknownStaff() {

        when(userDao.findById(999L))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response =
                controller.setActive(
                        999L,
                        Map.of("active", false)
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(userDao, never()).save(any(User.class));
    }

    // ---------------------------------------------------------
    // TC-STAFF-012
    // List should exclude ADMIN users
    // ---------------------------------------------------------

    @Test
    void TC_STAFF_012_listExcludesAdminUsers() {

        User admin = createUser(
                1L,
                "admin",
                "System Admin",
                "admin@gmail.com",
                Role.ADMIN,
                true
        );

        User dentist = createUser(
                2L,
                "dfernando",
                "Dr. Fernando",
                "fernando@gmail.com",
                Role.DENTIST,
                true
        );

        User receptionist = createUser(
                3L,
                "receptionist",
                "Priya Perera",
                "priya@gmail.com",
                Role.RECEPTIONIST,
                true
        );

        when(userDao.findAll())
                .thenReturn(
                        List.of(
                                admin,
                                dentist,
                                receptionist
                        )
                );

        var result = controller.list();

        assertEquals(2, result.size());

        assertTrue(
                result.stream()
                        .noneMatch(r ->
                                r.getRole() == Role.ADMIN)
        );
    }
}