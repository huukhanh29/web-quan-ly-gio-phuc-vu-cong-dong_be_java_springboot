package com.beton408.repository;

import com.beton408.entity.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification save(Notification notification);
    List<Notification> findByUserIdAndAndStatus(Long userId, String status);
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.id DESC")
    List<Notification> findByUserId(@Param("userId") Long userId);
    @Transactional
    void deleteAllByUser_IdAndAndStatus(Long userId, String status);
}
