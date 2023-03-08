package com.beton408.model;

import java.util.Date;

public class UserInfo {

    private final Long id;
    private final String username;
    private final String name;
    private final String email;
    private final Date date;
    private final int status;



    public UserInfo(Long id, String username,
                    String name, String email, String role,
                    Date date, String phone,
                    String avatar, int status) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.date = date;
        this.status = status;
        this.phone = phone;
        this.role = role;
        this.avatar = avatar;
    }

    private final String phone;
    private final String role;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Date getDate() {
        return date;
    }

    public int getStatus() {
        return status;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public String getAvatar() {
        return avatar;
    }

    private final String avatar;



    public UserInfo() {
        this.id = 0L;
        this.username = "";
        this.name = "";
        this.email ="";
        this.status = 1;
        this.phone ="";
        this.role = "USER";
        this.avatar = "User.png";
        this.date = null;
    }
}

