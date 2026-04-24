package com.example.taskmanager.service;

import com.example.taskmanager.dto.LoginRequest;
import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.entity.Organization;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.OrganizationRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtil;
import com.example.taskmanager.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;

    // ✅ Register user + organization
    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Create Organization
        Organization org = new Organization();
        org.setName(request.getOrganizationName());
        organizationRepository.save(org);

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordUtil.hashPassword(request.getPassword())); // ✅ only this
        user.setRole(request.getRole().toUpperCase());
        user.setOrganization(org);

        userRepository.save(user);

        return "User registered successfully";
    }

    // ✅ Login → return JWT
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(user);
    }
}