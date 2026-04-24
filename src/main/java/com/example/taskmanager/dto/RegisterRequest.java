package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String email;
    private String password;
    private String role; // ADMIN or MEMBER
    private String organizationName;
}