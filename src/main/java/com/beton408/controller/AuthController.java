package com.beton408.controller;

import com.beton408.entity.UserEntity;
import com.beton408.model.LoginRequest;
import com.beton408.model.JwtResponse;
import com.beton408.model.MessageResponse;
import com.beton408.model.SignUpRequest;
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

    @PostMapping(value = "/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequest loginModel){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginModel.getUsername(), loginModel.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());
//        System.out.println(roles);
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), roles.get(0),  userDetails.getUsername()));
    }
    @PostMapping(value = "/signup")
    public ResponseEntity<?> signup(@RequestBody @NonNull SignUpRequest registerModel){
        if (userService.isUsernameExist(registerModel.getUsername())) {
            return new ResponseEntity(new MessageResponse("ERROR: USERNAME WAS USED"), HttpStatus.BAD_REQUEST);
        }
        UserEntity user = new UserEntity(registerModel.getUsername(), encoder.encode(registerModel.getPassword()), registerModel.getRole());
        userService.addUser(user);
        return new ResponseEntity(user, HttpStatus.OK);
    }
}
