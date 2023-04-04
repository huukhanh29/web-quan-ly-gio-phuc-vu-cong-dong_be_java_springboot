package com.beton408.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "job_title")
public class JobTitleEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "required_hours")
    private int requiredHours;

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

    public int getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(int requiredHours) {
        this.requiredHours = requiredHours;
    }

    public JobTitleEntity(Long id, String name, int requiredHours) {
        this.id = id;
        this.name = name;
        this.requiredHours = requiredHours;
    }

    public JobTitleEntity() {
    }

}