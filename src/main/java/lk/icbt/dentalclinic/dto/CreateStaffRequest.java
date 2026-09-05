package lk.icbt.dentalclinic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.icbt.dentalclinic.model.Role;

/**
 * Submitted by the Admin to create a login for a new Dentist or
 * Receptionist. The username and a random temporary password are
 * generated server-side; the password is never chosen by the Admin so it
 * can be safely emailed to the new staff member and never has to be
 * shown or typed in the UI.
 */
public class CreateStaffRequest {

    @NotBlank
    private String displayName;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Role role;

    // No-argument constructor
    public CreateStaffRequest() {
    }

    // Getters

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    // Setters

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}