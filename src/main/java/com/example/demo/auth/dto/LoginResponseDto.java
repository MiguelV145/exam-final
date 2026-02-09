package com.example.demo.auth.dto;

import java.util.Set;

public class LoginResponseDto {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, Long userId, String username, String email, Set<String> roles) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
