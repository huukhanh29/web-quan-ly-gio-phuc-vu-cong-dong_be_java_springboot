package com.beton408.model;

public class SignUpRequest {
    private final String username;
    private final String password;
    private final String role = "USER";

    public String getRole() {
        return role;
    }

    public SignUpRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
