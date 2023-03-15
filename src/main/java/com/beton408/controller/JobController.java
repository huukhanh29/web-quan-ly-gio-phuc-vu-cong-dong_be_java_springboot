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
    @GetMapping("/chart-data/{userId}/{acdemic}")
    public Map<String, Integer> getChartData(@PathVariable Long userId,
                                             @PathVariable String acdemic) {
        UserAccumulatedHours userHours = hoursRepository.findByUserIdAndAcademicYear(userId, acdemic);
        JobTitleEntity jobTitle = jobRepository.findByName(userHours.getUser().getJobTitle().getName());
        int requiredHours = jobTitle.getRequiredHours();
        int totalHours = userHours.getTotalHours();
        int missHours =requiredHours-totalHours;
        if(missHours<0){
            missHours =0;
        }
        Map<String, Integer> chartData = new HashMap<>();
        chartData.put("missHours", missHours);
        chartData.put("totalHours", totalHours);
        chartData.put("requiredHours", requiredHours);

        return chartData;
    }
}
