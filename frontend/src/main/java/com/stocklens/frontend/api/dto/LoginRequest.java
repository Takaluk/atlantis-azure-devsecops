package com.stocklens.frontend.api.dto;

public record LoginRequest(
        String email,
        String password
) {
}
