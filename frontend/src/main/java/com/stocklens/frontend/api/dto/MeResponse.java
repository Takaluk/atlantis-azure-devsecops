package com.stocklens.frontend.api.dto;

public record MeResponse(
        Long userId,
        String email,
        String name
) {
}
