package com.beton408.model;

public class UserInfo {

    private final Long id ;
    private final String username;
    private final String role;

    public String getRole() {
        return role;
    }

    public UserInfo(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserInfo() {
        this.id =0L;
        this.username="";
        this.role = "USER";
    }
}
