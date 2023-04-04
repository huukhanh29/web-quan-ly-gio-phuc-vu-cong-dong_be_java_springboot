package com.beton408.controller;

import com.beton408.entity.*;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.ActivityOfUser;
import com.beton408.model.UserActvityInfo;
import com.beton408.repository.*;
import com.beton408.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.ArrayList;
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
    @Autowired
    private NotificationRepository notificationRepository;


    @GetMapping("/get/all")
    public Page<UserActivity> getAllFaqs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "activity.createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
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

    //cập nhật trạng thái đăng ký hoạt động của người dùng
    @PostMapping("/update/status/{id}")
    public ResponseEntity<?> updateStatus(@RequestBody ActivityOfUser activityOfUser,
                                          @PathVariable("id") Long id) {
        //tìm hoạt động đăng ký của user
        UserActivity userActivity = userActivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id=" + id));
        UserEntity user = userActivity.getUser();
        ActivityEntity activity = userActivity.getActivity();

        if (activity.getStatus().equals("Sắp diễn ra") && activityOfUser.getStatus().equals("Chờ xác nhận")) {
            userActivity.updateStatus(activityOfUser.getStatus());
            userActivityRepository.save(userActivity);
            //tạo thông báo duyệt
            Notification notification =new Notification();
            notification.setContent("Yêu cầu đăng ký tham gia hoạt động " +
                    activity.getName()+" của bạn đã được duyệt.");
            notification.setUser(user);
            notification.setTitle("Duyệt đăng ký hoạt động");
            notification.setStatus("Chưa đọc");
            notificationRepository.save(notification);
            return new ResponseEntity<>(userActivity, HttpStatus.OK);
        }
        Year currentYear = Year.now();
        int year = currentYear.getValue();
        String yearString = String.valueOf(year);
        if (activity.getStatus().equals("Đã kết thúc") && activityOfUser.getStatus().equals("Đã xác nhận")) {
            int totalHours =0;
            UserAccumulatedHours userAccumulatedHours = hoursRepository.findByUserIdAndAcademicYear(user.getId(), yearString);
            if (userAccumulatedHours != null) {
                userAccumulatedHours.setTotalHours(userAccumulatedHours.getTotalHours() +
                        userActivity.getActivity().getAccumulatedTime());
                hoursRepository.save(userAccumulatedHours);
                totalHours = userAccumulatedHours.getTotalHours();
            } else {
                UserAccumulatedHours userAccumulatedHours1 = new UserAccumulatedHours();
                userAccumulatedHours1.setTotalHours(userActivity.getActivity().getAccumulatedTime());
                userAccumulatedHours1.setAcademicYear(yearString);
                userAccumulatedHours1.setUser(user);
                hoursRepository.save(userAccumulatedHours1);
                totalHours = userAccumulatedHours.getTotalHours();
            }
            userActivity.updateStatus(activityOfUser.getStatus());
            //tạo thông báo xác nhận
            int hours =  userActivity.getActivity().getAccumulatedTime();
            Notification notification =new Notification();
            notification.setContent("Việc tham gia hoạt động " +
                    activity.getName()+" của bạn đã được xác nhận. Bạn đuợc cộng "+
                    hours + " giờ vào giờ tích lũy cộng đồng. Tổng giờ hiện tại của bạn là "+
                    totalHours+" giờ.");
            notification.setUser(user) ;
            notification.setTitle("Xác nhận tham gia hoạt động");
            notification.setStatus("Chưa đọc");
            notificationRepository.save(notification);
            userActivityRepository.save(userActivity);
            return new ResponseEntity<>(userActivity, HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid status update", HttpStatus.BAD_REQUEST);
    }

    //lấy danh sách hoạt động đã được đăng ký
    @GetMapping("/get/activity")
    public ResponseEntity<List<String>> getActivity() {
        List<String> activity = userActivityRepository.findDistinctActivity();
        return ResponseEntity.ok().body(activity);
    }

    //lấy thông tin người dùng khi đăng ký hoạt động
    @GetMapping("/get/user-info/{userId}")
    public ResponseEntity<UserActvityInfo> getUserInfo(@PathVariable Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id=" + userId));
        int totalHours = 0;
        Year currentYear = Year.now();
        int year = currentYear.getValue();
        String yearString = String.valueOf(year);
        UserAccumulatedHours userAccumulatedHours = hoursRepository.findByUserIdAndAcademicYear(userId, yearString);
        if (userAccumulatedHours != null) {
            totalHours = userAccumulatedHours.getTotalHours();
        }
        if (userEntity != null) {
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
    //hủy đăng ký một hoạt động
    @DeleteMapping("/destroy/{userId}/{activityId}")
    public ResponseEntity<?> destroyRegister(@PathVariable(value = "userId") Long userId,
                                             @PathVariable(value = "activityId") Long activityId,
                                             HttpServletRequest request) {
        UserActivity userActivity = userActivityRepository.findByActivityAndUserId(activityId, userId);
        if (userActivity == null) {
            return ResponseEntity.notFound().build();
        }

        //lấy ra curent user
        String token = JwtUtils.resolveToken(request);
        if (token == null || !JwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Claims claims = JwtUtils.getClaimsFromToken(token);
        Long currentUserId = Long.parseLong(String.valueOf(claims.get("id", Integer.class)));
        UserEntity currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new RuntimeException("User not found"));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if(currentUser.getRole().equals("LECTURER")){
            //tạo thông báo hủy
            Notification notification =new Notification();
            notification.setContent("Bạn đã hủy đăng ký tham gia hoạt động "+
                    userActivity.getActivity().getName() +".");
            notification.setUser(user);
            notification.setTitle("Hủy đăng ký hoạt động");
            notification.setStatus("Chưa đọc");
            notificationRepository.save(notification);
        }
        if(currentUser.getRole().equals("ADMIN")){
            //tạo thông báo
            if(userActivity.getStatus().equals("Chờ duyệt")){
                Notification notification =new Notification();
                notification.setContent("Yêu cầu đăng ký hoạt động "+
                        userActivity.getActivity().getName() +
                        "của bạn đã bị hủy.");
                notification.setUser(user);
                notification.setTitle("Hủy đăng ký hoạt động");
                notification.setStatus("Chưa đọc");
                notificationRepository.save(notification);
            }else if(userActivity.getStatus().equals("Chờ xác nhận")){
                Notification notification =new Notification();
                notification.setContent("Xác nhận tham gia hoạt động "+
                        userActivity.getActivity().getName() +
                        " của bạn thất bại.");
                notification.setUser(user);
                notification.setTitle("Hủy xác nhận hoạt động");
                notification.setStatus("Chưa đọc");
                notificationRepository.save(notification);
            }
        }
        userActivityRepository.delete(userActivity);
        return ResponseEntity.ok().build();
    }
    //lấy danh sách hoạt động của một người dùng đã xác nhận
    @GetMapping("/users/{userId}/activities")
    public List<ActivityEntity> getConfirmedActivitiesByUserId(@PathVariable Long userId) {
        List<ActivityEntity> confirmedActivities = new ArrayList<>();

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<UserActivity> userActivities = userActivityRepository.findByUserAndStatus(user, "Đã xác nhận");

        for (UserActivity userActivity : userActivities) {
            confirmedActivities.add(userActivity.getActivity());
        }

        return confirmedActivities;
    }

}
