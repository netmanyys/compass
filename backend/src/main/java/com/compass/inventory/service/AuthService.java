package com.compass.inventory.service;

import com.compass.inventory.dto.AuthDtos;
import com.compass.inventory.repository.UserRepository;
import com.compass.inventory.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String access = jwtService.generateAccessToken(request.username(), roles);
        String refresh = jwtService.generateRefreshToken(request.username(), roles);
        String role = roles.contains("ROLE_ADMIN") ? "admin" : "staff";
        return new AuthDtos.AuthResponse(access, refresh, jwtService.getAccessSeconds(), role);
    }

    public AuthDtos.RefreshResponse refresh(AuthDtos.RefreshRequest request) {
        var jwtUser = jwtService.parseToken(request.refreshToken());
        String access = jwtService.generateAccessToken(jwtUser.username(), jwtUser.roles());
        return new AuthDtos.RefreshResponse(access, jwtService.getAccessSeconds());
    }

    public AuthDtos.MeResponse me(String username) {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<String> roles = user.getRoles().stream().map(r -> r.getRole()).toList();
        String role = roles.contains("ROLE_ADMIN") ? "admin" : "staff";
        return new AuthDtos.MeResponse(user.getUsername(), roles, role);
    }
}
