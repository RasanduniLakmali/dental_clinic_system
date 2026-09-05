package lk.icbt.dentalclinic.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // No-argument constructor
    public LoginRequest() {
    }

    // Getters

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}