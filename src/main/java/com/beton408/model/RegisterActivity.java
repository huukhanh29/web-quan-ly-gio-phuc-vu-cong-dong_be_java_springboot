package com.beton408.model;

public class RegisterActivity {
    private  Long userId;
    private  Long activityId;

    public RegisterActivity() {
    }

    public RegisterActivity(Long userId, Long activityId) {
        this.userId = userId;
        this.activityId = activityId;
    }


    public Long getUserId() {
        return userId;
    }

    public Long getActivityId() {
        return activityId;
    }
}
