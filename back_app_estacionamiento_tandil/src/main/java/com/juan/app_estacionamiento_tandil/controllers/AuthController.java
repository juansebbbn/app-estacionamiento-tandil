package com.juan.app_estacionamiento_tandil.controllers;

import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Login_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Register_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Token_data_transfer;
import com.juan.app_estacionamiento_tandil.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================
    // register
    // =========================
    @PostMapping("/register")
    public Token_data_transfer register(@RequestBody Register_data_transfer dto) {
        return authService.register(dto);
    }

    // =========================
    // login
    // =========================
    @PostMapping("/login")
    public Token_data_transfer login(@RequestBody Login_data_transfer dto) {
        return authService.login(dto);
    }

    //=========================
    // refresh token
    // =========================
    @PostMapping("/refresh")
    public Token_data_transfer refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        return authService.refreshToken(authHeader);
    }


}
