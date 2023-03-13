package com.beton408.controller;

import com.beton408.entity.ActivityType;
import com.beton408.repository.ActivityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/activities/type")
@CrossOrigin(value = "*")
public class ActivityTypeController {
    @Autowired
    private ActivityTypeRepository activityTypeRepository;
    @GetMapping("/get/all")
    public ResponseEntity<?> getAllActivityTypes() {
        List<ActivityType> activityTypes = activityTypeRepository.findAll();
        return ResponseEntity.ok(activityTypes);
    }
}
