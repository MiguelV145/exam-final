package com.example.demo.auth.service;

import com.example.demo.auth.dto.LoginRequestDto;
import com.example.demo.auth.dto.LoginResponseDto;
import com.example.demo.auth.dto.RegisterRequestDto;

public interface AuthService {

    void register(RegisterRequestDto request);

    LoginResponseDto login(LoginRequestDto request);
}
