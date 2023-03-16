package com.beton408.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role ;
    @Column(nullable = true)
    private String avatar ;
    @Column(nullable = true)
    private String phone;
    @Column(nullable = true)
    private String gender;
    @Column(nullable = true)
    private String address;
    @Column(nullable = true)

    private Date dateOfBirth;
    @Column(nullable = false)
    private int status;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "job_title_id", nullable = true)
    private JobTitleEntity jobTitle;

    public JobTitleEntity getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(JobTitleEntity jobTitle) {
        this.jobTitle = jobTitle;
    }


    public UserEntity(int status) {
        this.status = status;
    }

    public UserEntity(Long id, String username, String email, String name, String password, String role, String avatar, String phone, String gender, String address, Date dateOfBirth, int status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.avatar = avatar;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserEntity(String username, String email, String name,
                      String password, String role, String avatar,
                      String gender, int status){
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.avatar = avatar;
        this.gender = gender;
        this.status = status;
}
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserEntity() {
    }
    public void updateUser(String phone, Date dateOfBirth, String gender, String address) {
        if (phone != null) {
            this.phone = phone;
        }
        if (dateOfBirth != null) {
            this.dateOfBirth = dateOfBirth;
        }
        if (gender != null) {
            this.gender = gender;
        }
        if (address != null) {
            this.address = address;
        }
        this.updatedAt = LocalDateTime.now();
    }
}
