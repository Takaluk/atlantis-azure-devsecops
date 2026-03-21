package com.stocklens.frontend.api;

import com.stocklens.frontend.api.dto.StockItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockGatewayController {

    private final RestClient stockRestClient;

    public StockGatewayController(@Qualifier("stockRestClient") RestClient stockRestClient) {
        this.stockRestClient = stockRestClient;
    }

    @GetMapping
    public List<StockItem> search(
            @RequestParam(required = false) String market,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit
    ) {
        var response = stockRestClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/api/stocks")
                            .queryParam("limit", limit);
                    if (market != null && !market.isBlank()) {
                        builder.queryParam("market", market);
                    }
                    if (q != null && !q.isBlank()) {
                        builder.queryParam("q", q);
                    }
                    return builder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<List<StockItem>>() {});
        return response != null ? response : List.of();
    }

    @GetMapping("/{symbol}")
    public Map<String, Object> detail(@PathVariable String symbol) {
        Map<String, Object> response = stockRestClient.get()
                .uri("/api/stocks/{symbol}", symbol)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        return response != null ? response : java.util.Map.of();
    }

    @GetMapping("/sparks")
    public List<Map<String, Object>> sparks(
            @RequestParam List<String> symbols,
            @RequestParam(defaultValue = "20") int limit
    ) {
        if (symbols == null || symbols.isEmpty()) {
            return List.of();
        }
        var response = stockRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stocks/sparks")
                        .queryParam("symbols", String.join(",", symbols))
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
        return response != null ? response : List.of();
    }
}
