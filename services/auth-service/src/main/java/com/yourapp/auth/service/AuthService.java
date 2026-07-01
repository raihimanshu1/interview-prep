package com.yourapp.auth.service;

import com.yourapp.auth.dto.AuthResponse;
import com.yourapp.auth.dto.LoginRequest;
import com.yourapp.auth.dto.RegisterRequest;

public interface AuthService {
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse login(LoginRequest request);
    
    AuthResponse refreshToken(String refreshToken);
}
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}