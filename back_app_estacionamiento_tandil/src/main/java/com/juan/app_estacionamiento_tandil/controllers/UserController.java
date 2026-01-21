package com.juan.app_estacionamiento_tandil.controllers;

import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getuser")
    public ResponseEntity<User> getUser(@AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return userService.getUserByUsername(username);
    }

}
