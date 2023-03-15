package com.beton408.repository;

import com.beton408.entity.UserAccumulatedHours;
import com.beton408.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccumulatedHoursRepository extends JpaRepository<UserAccumulatedHours, Long> {
    UserAccumulatedHours findByUserId(Long userId);
    UserAccumulatedHours findByUserIdAndAcademicYear(Long userId, String a);
}
