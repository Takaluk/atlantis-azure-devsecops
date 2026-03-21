package com.stocklens.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final RedisSessionStore sessionStore;

    public JwtAuthenticationFilter(JwtService jwtService, RedisSessionStore sessionStore) {
        this.jwtService = jwtService;
        this.sessionStore = sessionStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = resolveToken(request);
        if (token != null) {
            try {
                Claims claims = jwtService.parseClaims(token);
                var jti = claims.getId();
                if (jti == null || !sessionStore.exists(jti)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
                var userId = Long.valueOf(claims.getSubject());
                var email = claims.get("email", String.class);
                var principal = new UserPrincipal(userId, email);
                var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        principal, token, java.util.List.of());
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring("Bearer ".length()).trim();
    }
}
