package com.beton408.controller;

import com.beton408.entity.JobTitleEntity;
import com.beton408.entity.UserAccumulatedHours;
import com.beton408.entity.UserActivity;
import com.beton408.entity.UserEntity;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.MessageResponse;
import com.beton408.model.SignUpRequest;
import com.beton408.model.StatusRequest;
import com.beton408.repository.JobRepository;
import com.beton408.repository.UserAccumulatedHoursRepository;
import com.beton408.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user/job")
@CrossOrigin(value = "*")
public class JobController {
    @Autowired
    private UserAccumulatedHoursRepository hoursRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllActivityTypes() {
        List<JobTitleEntity> activityTypes = jobRepository.findAll();
        return ResponseEntity.ok(activityTypes);
    }

    //cập nhật chức danh
    @PutMapping("/update/{id}/{jobId}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @PathVariable Long jobId) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserEntity", "id", id));
        if (user.getJobTitle() != null) {
            if (jobId.equals(user.getJobTitle().getId())) {
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
        }
        JobTitleEntity jobTitle = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobTitleEntity", "id", jobId));
        user.setJobTitle(jobTitle);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    //lấy dữ liệu cho chart pie
    @GetMapping("/chart-data/{userId}/{acdemic}")
    public Map<String, Integer> getChartData(@PathVariable Long userId,
                                             @PathVariable String acdemic) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserEntity", "id", userId));
        UserAccumulatedHours userHours = hoursRepository.findByUserIdAndAcademicYear(userId, acdemic);
        int totalHours = 0;
        if (userHours != null) {
            totalHours = userHours.getTotalHours();
        }
        JobTitleEntity jobTitle = jobRepository.findByName(user.getJobTitle().getName());
        int requiredHours = jobTitle.getRequiredHours();

        int missHours = requiredHours - totalHours;
        if (missHours < 0) {
            missHours = 0;
        }
        Map<String, Integer> chartData = new HashMap<>();
        chartData.put("missHours", missHours);
        chartData.put("totalHours", totalHours);
        chartData.put("requiredHours", requiredHours);

        return chartData;
    }
}
