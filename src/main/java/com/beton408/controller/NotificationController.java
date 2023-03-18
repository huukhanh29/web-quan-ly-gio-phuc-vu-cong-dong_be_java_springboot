package com.beton408.controller;

import com.beton408.entity.Notification;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.MessageResponse;
import com.beton408.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(value = "*")
public class NotificationController {
    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getNotificationsByUserId(@PathVariable Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/status/{notiId}")
    public ResponseEntity<?> setStatusNotification(@PathVariable Long notiId) {
        Notification notifications = notificationRepository.findById(notiId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id=" + notiId));
        notifications.setStatus("Đã đọc");
        notificationRepository.save(notifications);
        return ResponseEntity.ok(notifications);
    }
    @DeleteMapping("/delete/{notiId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notiId) {
        Notification notifications = notificationRepository.findById(notiId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id=" + notiId));
        notificationRepository.delete(notifications);
        return ResponseEntity.ok(notifications);
    }
    @DeleteMapping("/delete/all/{userId}")
    public ResponseEntity<?> deleteNotificationsByUserId(@PathVariable Long userId) {
        List<Notification> notification = notificationRepository.findByUserIdAndAndStatus(userId, "Đã đọc");
        if(!notification.isEmpty()){
            notificationRepository.deleteAllByUser_IdAndAndStatus(userId, "Đã đọc");
            return ResponseEntity.ok("Deleted notifications for user with id " + userId);
        }else{
            return new ResponseEntity(new MessageResponse("NOT FOUND"), HttpStatus.BAD_REQUEST);
        }

    }
}
