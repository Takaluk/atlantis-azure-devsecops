package com.stocklens.user.api.dto;

public record MeResponse(
        Long userId,
        String email,
        String name
) {
}
