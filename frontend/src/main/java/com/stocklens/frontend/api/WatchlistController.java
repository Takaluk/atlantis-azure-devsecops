package com.stocklens.frontend.api;

import com.stocklens.frontend.api.dto.StockItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final RestClient authRestClient;
    private final RestClient stockRestClient;

    public WatchlistController(
            @Qualifier("authRestClient") RestClient authRestClient,
            @Qualifier("stockRestClient") RestClient stockRestClient
    ) {
        this.authRestClient = authRestClient;
        this.stockRestClient = stockRestClient;
    }

    @GetMapping
    public List<StockItem> list(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        List<Long> ids;
        try {
            ids = authRestClient.get()
                    .uri("/api/watchlist")
                    .header(HttpHeaders.AUTHORIZATION, authorization != null ? authorization : "")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Long>>() {});
        } catch (HttpClientErrorException.Unauthorized ex) {
            return List.of();
        }
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        var response = stockRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stocks/lookup")
                        .queryParam("ids", ids)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<StockItem>>() {});
        return response != null ? response : List.of();
    }

    @GetMapping("/exists")
    public boolean exists(
            @RequestParam Long stockId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        Boolean response;
        try {
            response = authRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/watchlist/exists")
                            .queryParam("stockId", stockId)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, authorization != null ? authorization : "")
                    .retrieve()
                    .body(Boolean.class);
        } catch (HttpClientErrorException.Unauthorized ex) {
            return false;
        }
        return response != null && response;
    }

    @PostMapping("/{stockId}")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(
            @PathVariable Long stockId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        try {
            authRestClient.post()
                    .uri("/api/watchlist/{stockId}", stockId)
                    .header(HttpHeaders.AUTHORIZATION, authorization != null ? authorization : "")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Void>() {});
        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), "auth service error");
        }
    }

    @DeleteMapping("/{stockId}")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(
            @PathVariable Long stockId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        try {
            authRestClient.delete()
                    .uri("/api/watchlist/{stockId}", stockId)
                    .header(HttpHeaders.AUTHORIZATION, authorization != null ? authorization : "")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Void>() {});
        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), "auth service error");
        }
    }
}
