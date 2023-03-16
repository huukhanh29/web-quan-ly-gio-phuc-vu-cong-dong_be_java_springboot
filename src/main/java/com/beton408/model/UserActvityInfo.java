package com.beton408.model;

public class UserActvityInfo {
    private String name;
    private String job;
    private int numActivities;
    private int totalHours;
    private int requiredHours;

    public UserActvityInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getNumActivities() {
        return numActivities;
    }

    public void setNumActivities(int numActivities) {
        this.numActivities = numActivities;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(int requiredHours) {
        this.requiredHours = requiredHours;
    }

    public UserActvityInfo(String name, String job, int numActivities, int totalHours, int requiredHours) {
        this.name = name;
        this.job = job;
        this.numActivities = numActivities;
        this.totalHours = totalHours;
        this.requiredHours = requiredHours;
    }
}
