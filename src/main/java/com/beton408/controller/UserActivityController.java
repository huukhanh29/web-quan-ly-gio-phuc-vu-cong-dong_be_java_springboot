package com.beton408.controller;

import com.beton408.entity.*;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.ActivityOfUser;
import com.beton408.model.UserActvityInfo;
import com.beton408.repository.ActivityTypeRepository;
import com.beton408.repository.UserAccumulatedHoursRepository;
import com.beton408.repository.UserActivityRepository;
import com.beton408.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/activities/manager")
@CrossOrigin(value = "*")
public class UserActivityController {
    @Autowired
    private ActivityTypeRepository activityTypeRepository;
    @Autowired
    private UserActivityRepository userActivityRepository;
    @Autowired
    private UserAccumulatedHoursRepository hoursRepository;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/get/all")
    public Page<UserActivity> getAllFaqs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false, defaultValue = "") String year,
            @RequestParam(required = false, defaultValue = "") String condition
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<UserActivity> spec = Specification.where(null);
        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("activity").get("name"), pattern),
                        criteriaBuilder.like(root.get("user").get("name"), pattern)
                );
            });
        }
        if (!status.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("activity").get("status"), status));
        }
        if (!condition.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), condition));
        }
        if (startTime != null) {
            LocalDateTime startTimes = LocalDate.parse(startTime).atStartOfDay();
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("activity").get("startTime"), startTimes));
        }
        if (endTime != null) {
            LocalDate date = LocalDate.parse(endTime);
            LocalDateTime endTimes = date.atTime(23, 59, 59);
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("activity").get("endTime"), endTimes));
        }
        if (!year.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                LocalDateTime startOfYear = LocalDateTime.of(Integer.parseInt(year), 1, 1, 0, 0, 0);
                LocalDateTime endOfYear = LocalDateTime.of(Integer.parseInt(year), 12, 31, 23, 59, 59);
                return criteriaBuilder.between(root.get("activity").get("startTime"), startOfYear, endOfYear);
            });
        }
        return userActivityRepository.findAll(spec, paging);
    }

    @PostMapping("/update/status/{id}")
    public ResponseEntity<?> updateStatus(@RequestBody ActivityOfUser activityOfUser,
                                          @PathVariable("id") Long id) {
        UserActivity userActivity = userActivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id=" + id));
        UserEntity user = userActivity.getUser();
        ActivityEntity activity = userActivity.getActivity();
        if (activity.getStatus().equals("Sắp diễn ra") && activityOfUser.getStatus().equals("Chờ xác nhận")) {
            userActivity.updateStatus(activityOfUser.getStatus());
            userActivityRepository.save(userActivity);
            return new ResponseEntity<>(userActivity, HttpStatus.OK);
        }
        Year currentYear = Year.now();
        int year = currentYear.getValue();
        String yearString = String.valueOf(year);
        if (activity.getStatus().equals("Đã kết thúc") && activityOfUser.getStatus().equals("Đã xác nhận")) {
            UserAccumulatedHours userAccumulatedHours = hoursRepository.findByUserIdAndAcademicYear(user.getId(), yearString);

            if (userAccumulatedHours != null) {
                userAccumulatedHours.setTotalHours(userAccumulatedHours.getTotalHours() +
                        userActivity.getActivity().getAccumulatedTime());
                hoursRepository.save(userAccumulatedHours);
            } else {
                UserAccumulatedHours userAccumulatedHours1 = new UserAccumulatedHours();
                userAccumulatedHours1.setTotalHours(userActivity.getActivity().getAccumulatedTime());
                userAccumulatedHours1.setAcademicYear(yearString);
                userAccumulatedHours1.setUser(user);
                hoursRepository.save(userAccumulatedHours1);
            }
            userActivity.updateStatus(activityOfUser.getStatus());
            userActivityRepository.save(userActivity);
            return new ResponseEntity<>(userActivity, HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid status update", HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/get/activity")
    public ResponseEntity<List<String>> getYears() {
        List<String> activity = userActivityRepository.findDistinctActivity();
        return ResponseEntity.ok().body(activity);
    }
    @GetMapping("/get/user-info/{userId}")
    public ResponseEntity<UserActvityInfo> getUserInfo(@PathVariable Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + userId));
        int totalHours = 0;
        Year currentYear = Year.now();
        int year = currentYear.getValue();
        String yearString = String.valueOf(year);
        UserAccumulatedHours userAccumulatedHours = hoursRepository.findByUserIdAndAcademicYear(userId, yearString);
        if(userAccumulatedHours != null){
            totalHours = userAccumulatedHours.getTotalHours();
        }
        if (userEntity != null ){
            int numActivities = userActivityRepository.countConfirmedActivitiesByUser(userId);
            UserActvityInfo userInfoDto = new UserActvityInfo();
            userInfoDto.setName(userEntity.getName());
            userInfoDto.setJob(userEntity.getJobTitle().getName());
            userInfoDto.setNumActivities(numActivities);
            userInfoDto.setRequiredHours(userEntity.getJobTitle().getRequiredHours());
            userInfoDto.setTotalHours(totalHours);
            return ResponseEntity.ok(userInfoDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/destroy/{userId}/{activityId}")
    public ResponseEntity<?> destroyRegister(@PathVariable(value = "userId") Long userId,
                                             @PathVariable(value = "activityId") Long activityId) {
        UserActivity userActivity = userActivityRepository.findByActivityAndUserId(activityId, userId);
        if (userActivity == null) {
            return ResponseEntity.notFound().build();
        }
        userActivityRepository.delete(userActivity);
        return ResponseEntity.ok().build();
    }
}
