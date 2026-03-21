package com.stocklens.frontend.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeController {

    private final RestClient newsRestClient;

    public HomeController(@Qualifier("newsRestClient") RestClient newsRestClient) {
        this.newsRestClient = newsRestClient;
    }

    @GetMapping("/home")
    public Map<String, Object> home() {
        Map<String, Object> response = newsRestClient.get()
                .uri("/api/home")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        return response != null ? response : java.util.Map.of("keywords", java.util.List.of(), "articles", java.util.List.of());
    }
}
