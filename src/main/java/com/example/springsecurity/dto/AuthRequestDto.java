package com.example.springsecurity.dto;


import lombok.Data;

@Data
public class AuthRequestDto {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }
}

