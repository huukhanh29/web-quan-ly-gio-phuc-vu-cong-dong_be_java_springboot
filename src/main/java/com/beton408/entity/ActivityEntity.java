package com.beton408.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity")
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "activity_type_id")
    private ActivityType activityType;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "accumulated_time", nullable = true)
    private int accumulatedTime;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Formula("(CASE WHEN start_time > now() THEN 'Sắp diễn ra' WHEN end_time > now() THEN 'Đang diễn ra' ELSE 'Đã kết thúc' END)")
    private String status;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }



    public ActivityEntity(Long id, ActivityType activityType, String name, String location, String description, LocalDateTime startTime, LocalDateTime endTime, int accumulatedTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.activityType = activityType;
        this.name = name;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.accumulatedTime = accumulatedTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getAccumulatedTime() {
        return accumulatedTime;
    }

    public void setAccumulatedTime(int accumulatedTime) {
        this.accumulatedTime = accumulatedTime;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ActivityEntity() {
    }

    public ActivityEntity(Long id, ActivityType activityType, String name, String location,
                          String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.activityType = activityType;
        this.name = name;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
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

    public ActivityEntity(Long id) {
        this.id = id;
    }
}
