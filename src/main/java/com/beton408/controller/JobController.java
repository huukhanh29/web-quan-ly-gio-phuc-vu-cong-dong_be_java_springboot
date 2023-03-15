package com.beton408.controller;

import com.beton408.entity.JobTitleEntity;
import com.beton408.entity.UserAccumulatedHours;
import com.beton408.entity.UserActivity;
import com.beton408.entity.UserEntity;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.repository.JobRepository;
import com.beton408.repository.UserAccumulatedHoursRepository;
import com.beton408.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/chart-data/{userId}")
    public Map<String, Integer> getChartData(@PathVariable Long userId) {
        Year currentYear = Year.now();
        int year = currentYear.getValue();
        String yearString = String.valueOf(year);
        UserAccumulatedHours userHours = hoursRepository.findByUserIdAndAcademicYear(userId, yearString);
        JobTitleEntity jobTitle = jobRepository.findByName(userHours.getUser().getJobTitle().getName());
        int requiredHours = jobTitle.getRequiredHours();
        int totalHours = userHours.getTotalHours();
        Map<String, Integer> chartData = new HashMap<>();
        chartData.put("requiredHours", requiredHours);
        chartData.put("totalHours", totalHours);

        return chartData;
    }
}
