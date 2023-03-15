package com.beton408.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "user_activity")
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private ActivityEntity activity;

    @Column(name = "status")
    private String status;

    public UserActivity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ActivityEntity getActivity() {
        return activity;
    }

    public void setActivity(ActivityEntity activity) {
        this.activity = activity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserActivity(Long id, UserEntity user, ActivityEntity activity, String status) {
        this.id = id;
        this.user = user;
        this.activity = activity;
        this.status = status;
    }
    public void updateStatus(String status) {
        if (status != null) {
            this.status = status;
        }
    }
    public UserActivity(Long id) {
        this.id = id;
    }
}

