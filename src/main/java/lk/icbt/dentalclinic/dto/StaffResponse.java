package lk.icbt.dentalclinic.dto;

import lk.icbt.dentalclinic.model.Role;
import lk.icbt.dentalclinic.model.User;

public class StaffResponse {

    private Long id;
    private String username;
    private String displayName;
    private String email;
    private Role role;
    private boolean active;

    /**
     * True only in the response returned right after creation, so the Admin
     * can see the temporary password once if the email could not be sent.
     */
    private String temporaryPassword;

    private boolean emailSent;

    // No-argument constructor
    public StaffResponse() {
    }

    // All-argument constructor
    public StaffResponse(
            Long id,
            String username,
            String displayName,
            String email,
            Role role,
            boolean active,
            String temporaryPassword,
            boolean emailSent) {

        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.role = role;
        this.active = active;
        this.temporaryPassword = temporaryPassword;
        this.emailSent = emailSent;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    // Static factory method
    public static StaffResponse from(User u) {
        return new StaffResponse(
                u.getId(),
                u.getUsername(),
                u.getDisplayName(),
                u.getEmail(),
                u.getRole(),
                u.isActive(),
                null,
                true
        );
    }

    // Static factory method for newly created staff
    public static StaffResponse created(
            User u,
            String temporaryPassword,
            boolean emailSent) {

        return new StaffResponse(
                u.getId(),
                u.getUsername(),
                u.getDisplayName(),
                u.getEmail(),
                u.getRole(),
                u.isActive(),
                temporaryPassword,
                emailSent
        );
    }
}