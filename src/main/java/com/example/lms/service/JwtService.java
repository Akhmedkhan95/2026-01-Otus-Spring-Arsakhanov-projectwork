package com.example.lms.service;

import com.example.lms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider tokenProvider;

    public String generateToken(Authentication authentication) {
        return tokenProvider.generateToken(authentication);
    }

    public String getUsernameFromToken(String token) {
        return tokenProvider.getUsernameFromToken(token);
    }

    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }
}