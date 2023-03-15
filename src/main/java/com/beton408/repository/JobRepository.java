package com.beton408.repository;

import com.beton408.entity.JobTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<JobTitleEntity, Long> {
    JobTitleEntity findByName(String name);
}