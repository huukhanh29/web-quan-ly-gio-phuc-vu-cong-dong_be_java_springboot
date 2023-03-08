package com.beton408.model;

public class StatusRequest {
    private final String username;
    private final String status;

    public StatusRequest(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }
}
