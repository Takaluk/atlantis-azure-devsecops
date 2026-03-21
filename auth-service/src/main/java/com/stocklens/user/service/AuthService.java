package com.stocklens.user.service;

import com.stocklens.user.api.dto.LoginRequest;
import com.stocklens.user.api.dto.LoginResponse;
import com.stocklens.user.domain.AppUserEntity;
import com.stocklens.user.infra.AppUserRepository;
import com.stocklens.user.security.JwtService;
import com.stocklens.user.security.RedisSessionStore;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisSessionStore sessionStore;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RedisSessionStore sessionStore
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.sessionStore = sessionStore;
    }

    public LoginResponse login(LoginRequest request) {
        var user = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "invalid credentials"));
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "invalid credentials");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(UNAUTHORIZED, "invalid credentials");
        }

        String token = jwtService.issueAccessToken(user.getId(), user.getEmail());
        var claims = jwtService.parseClaims(token);
        sessionStore.store(claims.getId(), user.getId(), jwtService.accessTokenTtl());

        Duration ttl = jwtService.accessTokenTtl();
        return new LoginResponse(token, "Bearer", ttl.toSeconds(), user.getEmail(), user.getName());
    }

    public void logout(String authHeader) {
        var token = extractToken(authHeader);
        try {
            var claims = jwtService.parseClaims(token);
            if (claims.getId() != null) {
                sessionStore.remove(claims.getId());
            }
        } catch (JwtException e) {
            throw new ResponseStatusException(UNAUTHORIZED, "invalid token");
        }
    }

    public AppUserEntity requireUserById(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "user not found"));
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(UNAUTHORIZED, "missing token");
        }
        return authHeader.substring("Bearer ".length()).trim();
    }
}
