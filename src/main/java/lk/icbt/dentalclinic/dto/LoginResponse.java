package lk.icbt.dentalclinic.dto;

import lk.icbt.dentalclinic.model.Role;

public class LoginResponse {

    private String username;
    private Role role;
    private String displayName;
    private String message;

    // No-argument constructor
    public LoginResponse() {
    }

    // All-argument constructor
    public LoginResponse(
            String username,
            Role role,
            String displayName,
            String message) {

        this.username = username;
        this.role = role;
        this.displayName = displayName;
        this.message = message;
    }

    // Getters

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMessage() {
        return message;
    }
}