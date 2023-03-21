package com.beton408.controller;

import com.beton408.entity.*;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.*;
import com.beton408.repository.JobRepository;
import com.beton408.repository.UserAccumulatedHoursRepository;
import com.beton408.repository.UserActivityRepository;
import com.beton408.repository.UserRepository;
import com.beton408.security.UserDetailsImpl;
import com.beton408.security.UserDetailsServiceImpl;
import com.beton408.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/user")
@CrossOrigin(value = "*")
public class UserController {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserAccumulatedHoursRepository userAccumulatedHoursRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserActivityRepository userActivityRepository;

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        UserEntity user = userService.get(id);
        if (user.getId() == null) {
            return new ResponseEntity<>(new MessageResponse("NOT FOUND"), HttpStatus.NOT_FOUND);
        }
        String job = "";
        if (user.getJobTitle() != null) {
            job = user.getJobTitle().getName();
        }
        return new ResponseEntity<>(new UserInfo(user.getId(), user.getUsername(),
                user.getName(), user.getEmail(), user.getRole(), user.getDateOfBirth(),
                user.getPhone(), user.getGender(), user.getAddress(), user.getAvatar(),
                user.getStatus(), job), HttpStatus.OK);
    }

    @GetMapping("/get/all")
    public Page<UserEntity> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) Long activityId
    ) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable paging = PageRequest.of(page, size, sort);

        Specification<UserEntity> spec = Specification.where(null);

        if (!searchTerm.isEmpty()) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) -> {
                String pattern = "%" + searchTerm + "%";
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("name"), pattern),
                        criteriaBuilder.like(root.get("username"), pattern),
                        criteriaBuilder.like(root.get("email"), pattern)
                );
            });
        }
        Specification<UserActivity> userActivitySpec = Specification.where(null);
        List<Long> userIds;
        if (activityId != null) {
            userActivitySpec = userActivitySpec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("activity").get("id"), activityId)
            );
            //System.out.println(userId);
            List<UserActivity> userActivities = userActivityRepository.findByActivityId(activityId);

            userIds = userActivities.stream()
                    .filter(ua -> !"Chờ duyệt".equals(ua.getStatus()))
                    .map(ua -> ua.getUser().getId())
                    .collect(Collectors.toList());

        } else {
            userIds = null;
        }
        if (userIds != null) {
            spec = spec.and((root, query, criteriaBuilder) -> root.get("id").in(userIds));
        }
        return userRepository.findAll(spec, paging);
    }

    //cập nhật trạng thái
    @PostMapping("/update/status")
    public ResponseEntity<?> updateStatus(@RequestBody StatusRequest statusRequest) {
        UserEntity user = userRepository.findByUsername(statusRequest.getUsername());
        if ("ADMIN".equals(user.getRole())) {
            return new ResponseEntity(new MessageResponse("ERROR"), HttpStatus.BAD_REQUEST);
        }
        if ("active".equals(statusRequest.getStatus())) {
            if (user.getStatus() == 1) {
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setStatus(1);
        } else if ("disable".equals(statusRequest.getStatus())) {
            if (user.getStatus() == 0) {
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setStatus(0);
        } else {
            return new ResponseEntity<>("Invalid status value", HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //cập nhật quyền
    @PostMapping("/update/role")
    public ResponseEntity<?> updateRole(@RequestBody RoleRequest roleRequest) {
        UserEntity user = userRepository.findByUsername(roleRequest.getUsername());
        if ("ADMIN".equals(user.getRole())) {
            return new ResponseEntity(new MessageResponse("ERROR"), HttpStatus.BAD_REQUEST);
        }

        if ("LECTURER".equals(roleRequest.getRole())) {
            if ("LECTURER".equals(user.getRole())) {
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setRole("LECTURER");
        } else if ("STUDENT".equals(roleRequest.getRole())) {
            if ("STUDENT".equals(user.getRole())) {
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setRole("STUDENT");
        } else if ("ADMIN".equals(roleRequest.getRole())) {
            if ("ADMIN".equals(user.getRole())) {
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setRole("ADMIN");

        } else {
            return new ResponseEntity<>("Invalid status value", HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //cập nhật thông tin
    @PutMapping("/update/profile/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id,
                                                 @RequestBody UserEntity userUpdate) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserEntity", "id", id));

        user.updateUser(userUpdate.getPhone(), userUpdate.getDateOfBirth(),
                userUpdate.getGender(), userUpdate.getAddress());
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    //đổi mật khẩu
    @PutMapping("/change_password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable("id") Long id,
                                            @RequestBody PasswordRequest passwordRequest) {
        // tìm kiếm người dùng theo id
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        // lấy thông tin người dùng từ cơ sở dữ liệu
        UserEntity user = optionalUser.get();
        // kiểm tra mật khẩu cũ của người dùng
        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            return new ResponseEntity(new MessageResponse("NOTMATCH"), HttpStatus.BAD_REQUEST);
        }
        // cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);

        // tạo lại JWT token mới với thông tin mới của người dùng
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), passwordRequest.getNewPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        // trả về token mới trong response
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), roles.get(0), userDetails.getUsername(), userDetails.getName(), userDetails.getEmail()));
    }
    //lấy danh sách năm có hoạt động cộng ccuar user
    @GetMapping("/get/academic-year/{userId}")
    public ResponseEntity<?> getAcademicYearsByUser(@PathVariable Long userId) {
        List<String> academicYears = userAccumulatedHoursRepository.findDistinctAcademicYearsByUser(userId);
        return ResponseEntity.ok(academicYears);
    }
    //lấy danh sách giảng viên khen thưởng hoặc khiển trách
    @GetMapping("/lecturers")
    public List<Map<String, Object>> getLecturers(@RequestParam(defaultValue = "#{T(java.time.Year).now().toString()}")
                                                      String academicYear,
                                                  @RequestParam(defaultValue = "Khen thưởng") String type) {
        List<UserAccumulatedHours> userAccumulatedHoursList = userAccumulatedHoursRepository.findByAcademicYearAndUser_Role(academicYear, "LECTURER");
        List<UserEntity> userEntityList = userRepository.findAll();
        List<UserEntity> lecturers = new ArrayList<>();
        if(type.equals("Khen thưởng")){
             lecturers = userAccumulatedHoursList.stream()
                    .filter(userAccumulatedHours -> userAccumulatedHours.getTotalHours() >= userAccumulatedHours.getUser().getJobTitle().getRequiredHours())
                    .map(UserAccumulatedHours::getUser)
                    .collect(Collectors.toList());
        }
        if(type.equals("Khiển trách")) {
            lecturers = userEntityList.stream()
                    .filter(user -> user.getRole().equals("LECTURER"))
                    .collect(Collectors.toList());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserEntity lecturer : lecturers) {
            Map<String, Object> lecturerMap = new HashMap<>();
            int countConfirm= userActivityRepository.countConfirmedActivitiesByUser(lecturer.getId());
            int totalHours = 0;
            UserAccumulatedHours userAccumulatedHours = userAccumulatedHoursRepository.findByAcademicYearAndUser_Id(academicYear, lecturer.getId());
            if(userAccumulatedHours != null){
                totalHours = userAccumulatedHours.getTotalHours();
            }
            lecturerMap.put("name", lecturer.getName());
            lecturerMap.put("username", lecturer.getUsername());
            lecturerMap.put("email", lecturer.getEmail());
            lecturerMap.put("countConfirm", countConfirm);
            lecturerMap.put("jobTitle", lecturer.getJobTitle().getName());
            lecturerMap.put("totalHours", totalHours);
            lecturerMap.put("requiredHours", lecturer.getJobTitle().getRequiredHours());
            result.add(lecturerMap);
        }

        return result;
    }
    //đếm tổng số lượng user
    @GetMapping("/count")
    public Long countUsers() {
        return userRepository.count();
    }
}
