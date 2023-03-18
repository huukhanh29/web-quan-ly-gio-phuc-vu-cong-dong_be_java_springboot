package com.beton408.repository;

import com.beton408.entity.UserAccumulatedHours;
import com.beton408.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAccumulatedHoursRepository extends JpaRepository<UserAccumulatedHours, Long> {
    UserAccumulatedHours findByUserId(Long userId);
    UserAccumulatedHours findByUserIdAndAcademicYear(Long userId, String a);
    @Query("SELECT DISTINCT uah.academicYear FROM UserAccumulatedHours uah WHERE uah.user.id = :userId ORDER BY uah.academicYear DESC")
    List<String> findDistinctAcademicYearsByUser(@Param("userId") Long userId);
    List<UserAccumulatedHours> findByAcademicYearAndUser_Role(String academicYear, String role);
    UserAccumulatedHours findByAcademicYearAndUser_Id(String acdemicYear, Long userId);
}
