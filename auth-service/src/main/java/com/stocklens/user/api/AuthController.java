package com.stocklens.user.api;

import com.stocklens.user.api.dto.LoginRequest;
import com.stocklens.user.api.dto.LoginResponse;
import com.stocklens.user.api.dto.MeResponse;
import com.stocklens.user.security.UserPrincipal;
import com.stocklens.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        var user = authService.requireUserById(principal.userId());
        return new MeResponse(user.getId(), user.getEmail(), user.getName());
    }
}
