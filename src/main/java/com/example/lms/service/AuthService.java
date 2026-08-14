package com.example.lms.service;

import com.example.lms.dto.LoginRequest;
import com.example.lms.dto.RegisterRequest;
import com.example.lms.entity.User;
import com.example.lms.exception.AlreadyExistsException;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;  // ← ИЗМЕНИЛИ: было JwtTokenProvider

    // Разрешённые роли для публичной регистрации
    private static final Set<String> ALLOWED_ROLES = Set.of("STUDENT", "TEACHER");

    public Map<String, String> register(RegisterRequest request) {
        // ПРОВЕРКА: запрет регистрации с привилегированными ролями
        if (!ALLOWED_ROLES.contains(request.getRole())) {
            throw new IllegalArgumentException("Регистрация с ролью " + request.getRole() + " запрещена");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email уже зарегистрирован");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.valueOf(request.getRole()))
                .build();

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtService.generateToken(authentication);  // ← ИЗМЕНИЛИ: было tokenProvider
        return Map.of("token", token, "message", "Регистрация успешна");
    }

    public Map<String, String> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtService.generateToken(authentication);  // ← ИЗМЕНИЛИ: было tokenProvider
        return Map.of("token", token);
    }
}