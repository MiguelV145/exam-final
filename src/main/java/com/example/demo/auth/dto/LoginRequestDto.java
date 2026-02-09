package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {
    @NotBlank(message = "El identificador (email o username) no puede estar vacío")
    private String identifier;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    public LoginRequestDto() {
    }

    public LoginRequestDto(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
