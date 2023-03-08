package com.beton408.model;

public class SignUpRequest {
    private final String username;
    private final String name;
    private final String email;
    private final String password;

    public String getEmail() {
        return email;
    }

    public SignUpRequest(String username, String name, String email, String password, String role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    private int status = 1;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private final String role;
    private String avatar = "User.png";

    public String getAvatar() {
        return avatar;
    }

    public String getRole() {
        return role;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
