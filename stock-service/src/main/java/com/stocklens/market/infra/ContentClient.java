package com.stocklens.market.infra;

import com.stocklens.market.api.dto.StockContentResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ContentClient {
    private static final StockContentResponse EMPTY = new StockContentResponse(
            java.util.List.of(),
            java.util.List.of(),
            java.util.List.of(),
            java.util.List.of(),
            null
    );

    private final RestClient restClient;

    public ContentClient(RestClient newsRestClient) {
        this.restClient = newsRestClient;
    }

    public StockContentResponse fetchContent(Long stockId) {
        try {
            StockContentResponse response = restClient.get()
                    .uri("/api/stocks/{stockId}/content", stockId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<StockContentResponse>() {});
            return response != null ? response : EMPTY;
        } catch (RuntimeException ex) {
            return EMPTY;
        }
    }
}
