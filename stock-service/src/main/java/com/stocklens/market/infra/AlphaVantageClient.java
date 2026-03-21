package com.stocklens.market.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlphaVantageClient {

    private static final Logger log = LoggerFactory.getLogger(AlphaVantageClient.class);
    private static final int MAX_LIMIT = 200;
    private static final int MIN_LIMIT = 2;

    private final RestClient restClient;
    private final String apiKey;
    private final int cacheTtlSeconds;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public AlphaVantageClient(
            @Qualifier("alphaVantageRestClient") RestClient restClient,
            @Value("${app.market-data.alpha-api-key:}") String apiKey,
            @Value("${app.market-data.alpha-cache-ttl-seconds:300}") int cacheTtlSeconds
    ) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    public List<DailyPrice> fetchDailyPrices(String symbol, String market, int limit) {
        if (symbol == null || symbol.isBlank()) {
            return List.of();
        }
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Alpha Vantage API key missing; skipping price fetch for {}", symbol);
            return List.of();
        }

        int safeLimit = Math.min(Math.max(limit, MIN_LIMIT), MAX_LIMIT);
        String cacheKey = symbol + "|" + (market == null ? "" : market);
        CacheEntry cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired(cacheTtlSeconds)) {
            return slice(cached.prices(), safeLimit);
        }

        Map<String, Object> response;
        try {
            response = restClient.get()
                    .uri(uriBuilder -> buildUri(uriBuilder, symbol, market))
                    .retrieve()
                    .body(Map.class);
        } catch (Exception ex) {
            log.warn("Alpha Vantage request failed for {}: {}", symbol, ex.getMessage());
            return List.of();
        }

        if (response == null) {
            log.warn("Alpha Vantage response empty for {}", symbol);
            return List.of();
        }
        if (response.containsKey("Note") || response.containsKey("Error Message") || response.containsKey("Information")) {
            log.warn("Alpha Vantage response warning for {}: {}", symbol, response);
            return List.of();
        }

        SeriesSpec spec = resolveSeriesSpec(symbol, market);
        List<DailyPrice> prices = parseSeries(response, spec.seriesKey(), spec.closeKey());
        if (prices.isEmpty()) {
            log.warn("Alpha Vantage returned no prices for {}: {}", symbol, response.keySet());
        }
        cache.put(cacheKey, new CacheEntry(Instant.now(), prices));
        return slice(prices, safeLimit);
    }

    private java.net.URI buildUri(org.springframework.web.util.UriBuilder uriBuilder, String symbol, String market) {
        SeriesSpec spec = resolveSeriesSpec(symbol, market);
        org.springframework.web.util.UriBuilder builder = uriBuilder
                .path("/query")
                .queryParam("function", spec.function())
                .queryParam("apikey", apiKey);

        if (spec.function().startsWith("TIME_SERIES")) {
            return builder
                    .queryParam("symbol", symbol)
                    .queryParam("outputsize", "compact")
                    .build();
        }

        if ("FX_DAILY".equals(spec.function())) {
            SymbolPair pair = parsePair(symbol);
            return builder
                    .queryParam("from_symbol", pair.base())
                    .queryParam("to_symbol", pair.quote())
                    .queryParam("outputsize", "compact")
                    .build();
        }

        if ("DIGITAL_CURRENCY_DAILY".equals(spec.function())) {
            SymbolPair pair = parsePair(symbol);
            return builder
                    .queryParam("symbol", pair.base())
                    .queryParam("market", pair.quote())
                    .build();
        }

        return builder.build();
    }

    private SeriesSpec resolveSeriesSpec(String symbol, String market) {
        String safeMarket = market == null ? "" : market.trim();
        if ("Forex".equalsIgnoreCase(safeMarket)) {
            return new SeriesSpec("FX_DAILY", "Time Series FX (Daily)", "4. close");
        }
        if ("Crypto".equalsIgnoreCase(safeMarket)) {
            SymbolPair pair = parsePair(symbol);
            String closeKey = "4a. close (" + pair.quote().toUpperCase(Locale.US) + ")";
            return new SeriesSpec("DIGITAL_CURRENCY_DAILY", "Time Series (Digital Currency Daily)", closeKey);
        }
        return new SeriesSpec("TIME_SERIES_DAILY", "Time Series (Daily)", "4. close");
    }

    private SymbolPair parsePair(String symbol) {
        String[] parts = symbol.split("/");
        if (parts.length == 2) {
            return new SymbolPair(parts[0], parts[1]);
        }
        return new SymbolPair(symbol, "USD");
    }

    private List<DailyPrice> parseSeries(Map<String, Object> response, String seriesKey, String closeKey) {
        Map<String, Object> series = asMap(response.get(seriesKey));
        if (series == null) {
            return List.of();
        }

        List<DailyPrice> items = new ArrayList<>(series.size());
        for (Map.Entry<String, Object> entry : series.entrySet()) {
            LocalDate date;
            try {
                date = LocalDate.parse(entry.getKey());
            } catch (Exception ex) {
                continue;
            }
            Map<String, Object> bar = asMap(entry.getValue());
            if (bar == null) {
                continue;
            }
            BigDecimal close = parseClose(bar, closeKey);
            if (close == null) {
                continue;
            }
            items.add(new DailyPrice(date, close));
        }

        items.sort(Comparator.comparing(DailyPrice::date));
        return items;
    }

    private BigDecimal parseClose(Map<String, Object> bar, String closeKey) {
        Object raw = bar.get(closeKey);
        if (raw == null) {
            Optional<String> fallbackKey = bar.keySet().stream()
                    .filter(k -> k.contains("close"))
                    .findFirst();
            if (fallbackKey.isPresent()) {
                raw = bar.get(fallbackKey.get());
            }
        }
        if (raw == null) {
            return null;
        }
        try {
            return new BigDecimal(raw.toString());
        } catch (Exception ex) {
            return null;
        }
    }

    private List<DailyPrice> slice(List<DailyPrice> prices, int limit) {
        if (prices == null || prices.isEmpty() || prices.size() <= limit) {
            return prices == null ? List.of() : prices;
        }
        return prices.subList(prices.size() - limit, prices.size());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    public record DailyPrice(LocalDate date, BigDecimal close) {}

    private record CacheEntry(Instant fetchedAt, List<DailyPrice> prices) {
        boolean isExpired(int ttlSeconds) {
            return fetchedAt.plusSeconds(ttlSeconds).isBefore(Instant.now());
        }
    }

    private record SeriesSpec(String function, String seriesKey, String closeKey) {}

    private record SymbolPair(String base, String quote) {}
}
