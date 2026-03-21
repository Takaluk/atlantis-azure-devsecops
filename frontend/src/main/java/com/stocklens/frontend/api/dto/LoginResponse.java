package com.stocklens.frontend.api.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String email,
        String name
) {
}
