package com.example.taskmanager.controller;

import com.example.taskmanager.dto.LoginRequest;
import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ✅ Register user + organization
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        // 🔍 Basic validation
        if (request.getEmail() == null || request.getPassword() == null || request.getRole() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Email, password and role are required"));
        }

        String result = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", result));
    }

    // ✅ Login user → returns JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // 🔍 Basic validation
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Email and password are required"));
        }

        String token = authService.login(request);

        return ResponseEntity.ok(
                Map.of("token", token)
        );
    }
}