package com.beton408.controller;

import com.beton408.entity.UserEntity;
import com.beton408.model.MessageResponse;
import com.beton408.model.UserInfo;
import com.beton408.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
@CrossOrigin(value ="*")
public class UserController {
    @Autowired
    private UserDetailsServiceImpl userService;
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id){
        UserEntity user = userService.get(id);
        if(user.getId() == null){
            return new ResponseEntity<>(new MessageResponse("NOT FOUND"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new UserInfo(user.getId(), user.getUsername(), user.getRole()), HttpStatus.OK);
    }
}
