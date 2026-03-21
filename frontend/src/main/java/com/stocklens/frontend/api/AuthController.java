package com.stocklens.frontend.api;

import com.stocklens.frontend.api.dto.LoginRequest;
import com.stocklens.frontend.api.dto.LoginResponse;
import com.stocklens.frontend.api.dto.MeResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RestClient authRestClient;

    public AuthController(@Qualifier("authRestClient") RestClient authRestClient) {
        this.authRestClient = authRestClient;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authRestClient.post()
                .uri("/api/auth/login")
                .body(request)
                .retrieve()
                .body(LoginResponse.class);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        authRestClient.post()
                .uri("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, authorization != null ? authorization : "")
                .retrieve()
                .body(new ParameterizedTypeReference<Void>() {});
    }

    @GetMapping("/me")
    public MeResponse me(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return authRestClient.get()
                .uri("/api/auth/me")
                .header(HttpHeaders.AUTHORIZATION, authorization != null ? authorization : "")
                .retrieve()
                .body(MeResponse.class);
    }
}
