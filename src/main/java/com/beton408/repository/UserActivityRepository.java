package com.beton408.repository;

import com.beton408.entity.ActivityEntity;
import com.beton408.entity.HistoryEntity;
import com.beton408.entity.UserActivity;
import com.beton408.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long>, JpaSpecificationExecutor<UserActivity> {
    Long countByActivity(ActivityEntity activity);
    Page<UserActivity> findAll(Specification<UserActivity> spec, Pageable paging);
    @Query("SELECT ua FROM UserActivity ua WHERE ua.user.id = :userId")
    List<UserActivity> findByUserId(@Param("userId") Long userId);
    @Query("SELECT ua FROM UserActivity ua WHERE ua.activity.id = :activityId")
    List<UserActivity> findByActivityId(@Param("activityId") Long activityId);
    //cần kiểm tra
    @Query("SELECT ua FROM UserActivity ua WHERE ua.activity.id = :activityId AND ua.user.id = :userId")
    UserActivity findByActivityAndUserId(@Param("activityId") Long activityId,
                                               @Param("userId") Long userId);
    @Query("SELECT ua FROM UserActivity ua WHERE ua.status = :state")
    List<UserActivity> activityManager(@Param("state") String state);

    @Query("SELECT DISTINCT h.activity.name FROM UserActivity h ORDER BY h.activity.name ASC")
    List<String> findDistinctActivity();
    @Query("SELECT COUNT(ua) FROM UserActivity ua WHERE ua.user.id = :userId AND ua.status = 'Đã xác nhận'")
    int countConfirmedActivitiesByUser(@Param("userId") Long userId);
    List<UserActivity> findByUserAndStatus(UserEntity user, String status);
}