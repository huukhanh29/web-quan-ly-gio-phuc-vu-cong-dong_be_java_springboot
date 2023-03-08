package com.beton408.controller;

import com.beton408.entity.FaqEntity;
import com.beton408.entity.UserEntity;
import com.beton408.exception.ResourceNotFoundException;
import com.beton408.model.MessageResponse;
import com.beton408.model.RoleRequest;
import com.beton408.model.StatusRequest;
import com.beton408.model.UserInfo;
import com.beton408.repository.FaqRepository;
import com.beton408.repository.UserRepository;
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
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
@CrossOrigin(value ="*")
public class UserController {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id){
        UserEntity user = userService.get(id);
        if(user.getId() == null){
            return new ResponseEntity<>(new MessageResponse("NOT FOUND"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new UserInfo(user.getId(), user.getUsername(),
                user.getName(),user.getEmail(), user.getRole(),user.getDateOfBirth(),
                user.getPhone(), user.getAvatar(), user.getStatus()), HttpStatus.OK);
    }
    @GetMapping("/get/all")
    public Page<UserEntity> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false, defaultValue = "") String searchTerm
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
        return userRepository.findAll(spec, paging);
    }
    @PostMapping("/update/status")
    public ResponseEntity<?> updateStatus(@RequestBody StatusRequest statusRequest) {
        UserEntity user = userRepository.findByUsername(statusRequest.getUsername());
        if("ADMIN".equals(user.getRole())){
            return new ResponseEntity(new MessageResponse("ERROR"), HttpStatus.BAD_REQUEST);
        }
        if("active".equals(statusRequest.getStatus())){
            if(user.getStatus()==1){
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setStatus(1);
        } else if("disable".equals(statusRequest.getStatus())){
            if(user.getStatus()==0){
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setStatus(0);
        } else {
            return new ResponseEntity<>("Invalid status value", HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @PostMapping("/update/role")
    public ResponseEntity<?> updateRole(@RequestBody RoleRequest roleRequest) {
        UserEntity user = userRepository.findByUsername(roleRequest.getUsername());
        if("ADMIN".equals(user.getRole())){
            return new ResponseEntity(new MessageResponse("ERROR"), HttpStatus.BAD_REQUEST);
        }

        if("LECTURER".equals(roleRequest.getRole())){
            if("LECTURER".equals(user.getRole())){
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setRole("LECTURER");
        } else if("STUDENT".equals(roleRequest.getRole())){
            if("STUDENT".equals(user.getRole())){
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setRole("STUDENT");
        }
        else if("ADMIN".equals(roleRequest.getRole())){
            if("ADMIN".equals(user.getRole())){
                return new ResponseEntity(new MessageResponse("WARNING"), HttpStatus.OK);
            }
            user.setRole("ADMIN");

        }else {
            return new ResponseEntity<>("Invalid status value", HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
