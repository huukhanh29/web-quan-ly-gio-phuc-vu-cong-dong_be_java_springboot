package com.beton408.controller;

import com.beton408.entity.ActivityEntity;
import com.beton408.entity.ActivityType;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.ActivityResponse;
import com.beton408.repository.ActivityRepository;
import com.beton408.repository.ActivityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "/activities")
@CrossOrigin(value = "*")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ActivityTypeRepository activityTypeRepository;
    @GetMapping("/get/all")
    public Page<ActivityEntity> getAllFaqs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);
        Specification<ActivityEntity> spec = Specification.where(null);
        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("name"), pattern)
                );
            });
        }


        if (!status.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
        }
        if (startTime != null) {
            LocalDateTime startTimes = LocalDate.parse(startTime).atStartOfDay();
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startTimes));
        }
        if (endTime != null) {
            LocalDate date = LocalDate.parse(endTime);
            LocalDateTime endTimes = date.atTime(23, 59, 59);
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), endTimes));
        }
        return activityRepository.findAll(spec, paging);
    }
    @PostMapping("/create")
    public ResponseEntity<ActivityEntity> createActivity(@RequestBody ActivityResponse activityResponse) {
        // Lấy activityType từ activityTypeRepository dựa trên tên
        ActivityType activityType = activityTypeRepository.findActivityTypeByName(activityResponse.getActivityType());
        if (activityType == null) {
            throw new ResourceNotFoundException("ActivityType not found for this name :: " + activityResponse.getActivityType());
        }

        ActivityEntity activity = new ActivityEntity();
        activity.setActivityType(activityType);
        activity.setName(activityResponse.getName());
        activity.setLocation(activityResponse.getLocation());
        activity.setDescription(activityResponse.getDescription());
        activity.setStartTime(activityResponse.getStartTime());
        activity.setEndTime(activityResponse.getEndTime());
        activity.setAccumulatedTime(activityResponse.getAccumulatedTime());
        // Lưu activity vào database
        ActivityEntity savedActivity = activityRepository.save(activity);
        return ResponseEntity.ok(savedActivity);
    }

}
