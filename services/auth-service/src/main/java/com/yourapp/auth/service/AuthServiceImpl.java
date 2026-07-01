package com.yourapp.auth.service;

import com.yourapp.auth.dto.AuthResponse;
import com.yourapp.auth.dto.LoginRequest;
import com.yourapp.auth.dto.RegisterRequest;
import com.yourapp.auth.entity.UserEntity;
import com.yourapp.auth.repository.UserRepository;
import com.yourapp.auth.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        UserEntity user = UserEntity.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .roles(request.getRoles() != null ? request.getRoles() : List.of(UserEntity.Role.USER))
            .build();

        UserEntity saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRoles());
        Long expiresIn = jwtUtil.getExpirationInSeconds();

        return AuthResponse.builder()
            .accessToken(token)
            .email(saved.getEmail())
            .fullName(saved.getFullName())
            .roles(saved.getRoles().stream().map(Enum::name).toList())
            .expiresIn(expiresIn)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getPassword()
            )
        );

        UserEntity user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());
        Long expiresIn = jwtUtil.getExpirationInSeconds();

        return AuthResponse.builder()
            .accessToken(token)
            .email(user.getEmail())
            .fullName(user.getFullName())
            .roles(user.getRoles().stream().map(Enum::name).toList())
            .expiresIn(expiresIn)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtUtil.extractEmail(refreshToken);
        List<UserEntity.Role> roles = jwtUtil.extractRoles(refreshToken);

        if (email == null || jwtUtil.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        String newToken = jwtUtil.generateToken(user.getEmail(), user.getRoles());
        Long expiresIn = jwtUtil.getExpirationInSeconds();

        return AuthResponse.builder()
            .accessToken(newToken)
            .email(user.getEmail())
            .fullName(user.getFullName())
            .roles(user.getRoles().stream().map(Enum::name).toList())
            .expiresIn(expiresIn)
            .build();
    }
}
