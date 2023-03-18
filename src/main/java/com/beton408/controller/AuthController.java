package com.beton408.controller;

import com.beton408.entity.JobTitleEntity;
import com.beton408.entity.UserEntity;
import com.beton408.model.*;
import com.beton408.repository.JobRepository;
import com.beton408.security.UserDetailsImpl;
import com.beton408.security.UserDetailsServiceImpl;
import com.beton408.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/auth")
@CrossOrigin(value ="*")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    UserDetailsServiceImpl userService;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JobRepository jobRepository;
    //đăng nhập
    @PostMapping(value = "/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequest loginModel){
        UserEntity user;
        if (loginModel.getUsername().contains("@")) {
            user = userService.getByEmail(loginModel.getUsername());
        } else {
            user = userService.getByUsername(loginModel.getUsername());
        }
        if (user == null) {
            return new ResponseEntity<>(new MessageResponse("INVALID USERNAME OR EMAIL"), HttpStatus.BAD_REQUEST);
        }
        if (user.getStatus() == 0) {
            return new ResponseEntity<>(new MessageResponse("ACCOUNT HAS BEEN BLOCKED"), HttpStatus.BAD_REQUEST);
        }
        if (!passwordEncoder.matches(loginModel.getPassword(), user.getPassword())) {
            return new ResponseEntity<>(new MessageResponse("INVALID PASSWORD"), HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginModel.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), roles.get(0), userDetails.getUsername(), userDetails.getName(), userDetails.getEmail()));
    }
    //đăng ký
    @PostMapping(value = "/signup")
    public ResponseEntity<?> signup(@RequestBody @NonNull SignUpRequest registerModel){
        // Check if the email or username already exists
        if (userService.isEmailExist(registerModel.getEmail())) {
            return new ResponseEntity(new MessageResponse("ERROR: EMAIL WAS USED"), HttpStatus.BAD_REQUEST);
        } else if (userService.isUsernameExist(registerModel.getUsername())) {
            return new ResponseEntity(new MessageResponse("ERROR: USERNAME WAS USED"), HttpStatus.BAD_REQUEST);
        }


        // Create a new user entity
        UserEntity user = new UserEntity(registerModel.getUsername(),registerModel.getEmail(),
                registerModel.getName(), encoder.encode(registerModel.getPassword()),
                registerModel.getRole(), registerModel.getAvatar(),
                registerModel.getGender(), registerModel.getStatus() );
        if(registerModel.getJob()!= null){
            JobTitleEntity jobTitle = jobRepository.findByName(registerModel.getJob());
            user.setJobTitle(jobTitle);
        }
        // Add the new user to the database
        userService.addUser(user);

        // Create a sign-up response
        SignUpResponse userInfo = new SignUpResponse(user.getUsername(), user.getName(), user.getEmail(), user.getRole());
        // Return the sign-up response
        return new ResponseEntity(userInfo, HttpStatus.OK);
    }

}
