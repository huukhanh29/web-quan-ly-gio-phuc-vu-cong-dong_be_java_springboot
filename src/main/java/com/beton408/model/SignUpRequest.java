package com.beton408.model;

public class SignUpRequest {
    private final String username;
    private final String name;
    private final String email;
    private final String password;
    private final String gender;

    public String getGender() {
        return gender;
    }
    private String address;

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public SignUpRequest(String username, String name, String email,
                         String password, String gender, String address, String role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.address =address;
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
