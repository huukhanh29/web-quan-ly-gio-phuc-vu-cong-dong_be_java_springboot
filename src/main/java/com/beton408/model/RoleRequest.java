package com.beton408.model;

public class RoleRequest {
    private  final String username;
    private final  String role;

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public RoleRequest(String username, String role) {
        this.username = username;
        this.role = role;
    }
}
