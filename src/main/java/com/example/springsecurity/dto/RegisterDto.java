package com.example.springsecurity.dto;


import lombok.Data;

@Data
public class RegisterDto {

    private String surname;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;


    public String getName() {
        return email;
    }
}
