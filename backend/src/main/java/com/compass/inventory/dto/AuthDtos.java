package com.compass.inventory.dto;

import java.util.List;

public class AuthDtos {
    public record LoginRequest(String username, String password) {}

    public record AuthResponse(String accessToken, String refreshToken, long expiresInSeconds, String role) {}

    public record RefreshRequest(String refreshToken) {}

    public record RefreshResponse(String accessToken, long expiresInSeconds) {}

    public record MeResponse(String username, List<String> roles, String role) {}
}
