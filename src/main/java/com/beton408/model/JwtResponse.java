package com.beton408.model;

public class JwtResponse {
    private String token;

    private String type = "Bearer";
    private Long id;
    private String role;
    private String username;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public JwtResponse(String token, Long id, String role, String username) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.role = role;
        this.username = username;
    }
    public JwtResponse() {
    }
}
