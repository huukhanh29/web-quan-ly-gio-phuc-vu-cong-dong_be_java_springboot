package com.beton408.model;

import java.util.Date;

public class UserInfo {

    private final Long id;
    private final String username;
    private final String name;
    private final String email;
    private final Date date;
    private final int status;
    private final String gender;
private  final String job;

    public String getJob() {
        return job;
    }

    public String getGender() {
        return gender;
    }

    public UserInfo(Long id, String username,
                    String name, String email, String role,
                    Date date, String phone, String gender,
                    String address,
                    String avatar, int status, String job) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.date = date;
        this.status = status;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.role = role;
        this.avatar = avatar;
        this.job = job;
    }
    public String getAddress() {
        return address;
    }

    private final String address;

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
        this.address="";
        this.gender="";
        this.job ="";
    }
}

