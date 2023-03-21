package com.beton408.controller;

import com.beton408.entity.ActivityEntity;
import com.beton408.entity.ActivityType;
import com.beton408.entity.UserActivity;
import com.beton408.entity.UserEntity;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.*;
import com.beton408.repository.ActivityRepository;
import com.beton408.repository.ActivityTypeRepository;
import com.beton408.repository.UserActivityRepository;
import com.beton408.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/activities")
@CrossOrigin(value = "*")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ActivityTypeRepository activityTypeRepository;
    @Autowired
    private UserActivityRepository userActivityRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/get/all")
    public Page<ActivityEntity> getAllFaqs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Long userId
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

        Specification<UserActivity> userActivitySpec = Specification.where(null);
        List<Long> activityIds;
        if (userId != null) {
            userActivitySpec = userActivitySpec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("id"), userId)
            );
            //System.out.println(userId);
            List<UserActivity> userActivities = userActivityRepository.findByUserId(userId);
            activityIds = userActivities.stream()
                    .map(ua -> ua.getActivity().getId())
                    .collect(Collectors.toList());

        } else {
            activityIds = null;
        }
        if (activityIds != null) {
            spec = spec.and((root, query, criteriaBuilder) -> root.get("id").in(activityIds));
        }

        return activityRepository.findAll(spec, paging);
    }
    //tạo mới hoạt động
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
    //cập nhật hoạt động
    @PutMapping("/update/{id}")
    public ResponseEntity<ActivityEntity> updateActivity(@PathVariable(value = "id") Long activityId,
                                                         @RequestBody ActivityResponse activityResponse) {
        // Kiểm tra xem hoạt động có tồn tại không
        ActivityEntity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found for this id :: " + activityId));

        // Lấy activityType từ activityTypeRepository dựa trên tên
        ActivityType activityType = activityTypeRepository.findActivityTypeByName(activityResponse.getActivityType());
        if (activityType == null) {
            throw new ResourceNotFoundException("ActivityType not found for this name :: " + activityResponse.getActivityType());
        }

        // Cập nhật thông tin hoạt động
        activity.setActivityType(activityType);
        activity.setName(activityResponse.getName());
        activity.setLocation(activityResponse.getLocation());
        activity.setDescription(activityResponse.getDescription());
        activity.setStartTime(activityResponse.getStartTime());
        activity.setEndTime(activityResponse.getEndTime());
        activity.setAccumulatedTime(activityResponse.getAccumulatedTime());

        // Lưu hoạt động vào database
        final ActivityEntity updatedActivity = activityRepository.save(activity);

        return ResponseEntity.ok(updatedActivity);
    }
    //xóa hoạt động
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable("id") Long id) {
        ActivityEntity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id=" + id));

        // Kiểm tra hoạt động đã được sử dụng trong bảng UserActivity hay chưa
        Long count = userActivityRepository.countByActivity(activity);
        if (count > 0) {
            return new ResponseEntity(new
                    MessageResponse("ISUSE"),
                    HttpStatus.CONFLICT);
        }

        activityRepository.delete(activity);
        return ResponseEntity.ok().build();
    }
    //đăng ký hoạt động của người dùng
    @PostMapping("/register")
    public ResponseEntity<?> registerActivity(@RequestBody RegisterActivity registerActivity) {
        ActivityEntity activity = activityRepository.findById(registerActivity.getActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id=" + registerActivity.getActivityId()));
        UserEntity user = userRepository.findById(registerActivity.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id=" + registerActivity.getUserId()));
        UserActivity userActivity = new UserActivity();
        UserActivity checkActivity = userActivityRepository.findByActivityAndUserId(registerActivity.getActivityId(), registerActivity.getUserId());
        if(checkActivity != null) {
            return new ResponseEntity(new
                    MessageResponse("IS USE"),
                    HttpStatus.CONFLICT);
        }
        userActivity.setActivity(activity);
        userActivity.setUser(user);
        userActivity.setStatus("Chờ duyệt");
        userActivityRepository.save(userActivity);
        return ResponseEntity.ok(userActivity);
    }
    //lấy danh sách hoạt động của một người dùng
    @GetMapping("/get/of/{userId}")
    public ResponseEntity<?> getActivityOfUser(@PathVariable("userId") Long userId) {
        List<UserActivity> userActivities = userActivityRepository.findByUserId(userId);
        List<Long> activityIds = userActivities.stream()
                .map(ua -> ua.getActivity().getId())
                .collect(Collectors.toList());

        List<ActivityOfUser> activityDTOs = new ArrayList<>();
        for (UserActivity userActivity : userActivities) {
            ActivityEntity activityEntity = userActivity.getActivity();
            ActivityOfUser activityDTO = new ActivityOfUser();
            activityDTO.setId(activityEntity.getId());
            activityDTO.setActivityType(activityEntity.getActivityType());
            activityDTO.setName(activityEntity.getName());
            activityDTO.setLocation(activityEntity.getLocation());
            activityDTO.setDescription(activityEntity.getDescription());
            activityDTO.setStartTime(activityEntity.getStartTime());
            activityDTO.setEndTime(activityEntity.getEndTime());
            activityDTO.setAccumulatedTime(activityEntity.getAccumulatedTime());
            activityDTO.setStatus(userActivity.getStatus());
            activityDTOs.add(activityDTO);
        }

        return ResponseEntity.ok(activityDTOs);
    }
    //Lấy danh sách năm có trong danh sách hoạt động
    @GetMapping("/get/years")
    public List<Integer> getYears() {
        return activityRepository.findYears();
    }
    //lấy tổng số hoạt động sắp diễn ra
    @GetMapping("/upcoming-activities/count")
    public Long countUpcomingActivities() {
        return activityRepository.countByStatus("Sắp diễn ra");
    }
    @GetMapping("/count")
    public Long countActivity() {
        return activityRepository.count();
    }
}
