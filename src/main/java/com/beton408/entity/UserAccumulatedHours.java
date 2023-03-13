package com.beton408.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "user_accumulated_hours")
public class UserAccumulatedHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "total_hours")
    private int totalHours;

    @Column(name = "academic_year")
    private String academicYear;

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

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public UserAccumulatedHours() {
    }

    public UserAccumulatedHours(Long id, UserEntity user, int totalHours, String academicYear) {
        this.id = id;
        this.user = user;
        this.totalHours = totalHours;
        this.academicYear = academicYear;
    }
}

