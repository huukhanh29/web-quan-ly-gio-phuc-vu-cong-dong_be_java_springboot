package com.beton408.model;

import java.time.LocalDateTime;

public class ActivityResponse {
    private Long id;
    private String name;
    private String location;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int accumulatedTime;
    private String activityType;

    public ActivityResponse(Long id, String name, String location, String description,
                            LocalDateTime startTime, LocalDateTime endTime, int accumulatedTime, String activityType) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.accumulatedTime = accumulatedTime;
        this.activityType = activityType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getAccumulatedTime() {
        return accumulatedTime;
    }

    public void setAccumulatedTime(int accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public ActivityResponse() {
    }
}
