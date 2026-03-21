package com.stocklens.market.api;

import com.stocklens.market.api.dto.ArticleInfo;
import com.stocklens.market.api.dto.EventArticleInfo;
import com.stocklens.market.api.dto.ForecastInfo;
import com.stocklens.market.api.dto.KeywordInfo;
import com.stocklens.market.api.dto.StockContentResponse;
import com.stocklens.market.infra.ContentClient;
import com.stocklens.market.infra.StockRepository;
import com.stocklens.market.infra.AlphaVantageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockController {

    private final StockRepository stockRepository;
    private final ContentClient contentClient;
    private final AlphaVantageClient alphaVantageClient;

    @GetMapping
    public List<StockItem> search(
            @RequestParam(required = false) String market,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit
    ) {
        String mq = (q == null || q.isBlank()) ? null : q.trim();
        String mm = (market == null || market.isBlank()) ? null : market.trim();

        return stockRepository.search(mm, mq, Math.min(Math.max(limit, 1), 50))
                .stream()
                .map(s -> new StockItem(s.getId(), s.getSymbol(), s.getName(), s.getMarket(), s.getTag()))
                .toList();
    }

    @GetMapping("/{symbol}")
    public StockDetail detail(@PathVariable String symbol) {
        var stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "stock not found: " + symbol));

        var prices = alphaVantageClient.fetchDailyPrices(stock.getSymbol(), stock.getMarket(), 22)
                .stream()
                .map(p -> new PricePoint(p.date().toString(), p.close()))
                .toList();

        StockContentResponse content = contentClient.fetchContent(stock.getId());
        var events = content.events() == null ? List.<PriceEvent>of() : content.events().stream()
                .map(e -> new PriceEvent(e.id(), e.title(), e.type(), e.startDate(), e.endDate()))
                .toList();
        events = alignEventsToSeries(events, prices);

        return new StockDetail(
                new StockItem(stock.getId(), stock.getSymbol(), stock.getName(), stock.getMarket(), stock.getTag()),
                prices,
                events,
                content.keywords(),
                content.articles(),
                content.eventArticles(),
                content.forecast()
        );
    }

    @GetMapping("/lookup")
    public List<StockItem> lookup(@RequestParam List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return stockRepository.findAllById(ids).stream()
                .map(s -> new StockItem(s.getId(), s.getSymbol(), s.getName(), s.getMarket(), s.getTag()))
                .toList();
    }

    @GetMapping("/sparks")
    public List<SparkSeries> sparks(
            @RequestParam List<String> symbols,
            @RequestParam(defaultValue = "20") int limit
    ) {
        if (symbols == null || symbols.isEmpty()) {
            return List.of();
        }
        int safeLimit = Math.min(Math.max(limit, 2), 60);
        var dedup = symbols.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        if (dedup.isEmpty()) {
            return List.of();
        }

        var map = new java.util.LinkedHashMap<String, List<PricePoint>>();
        dedup.forEach(sym -> map.put(sym, new java.util.ArrayList<>()));
        var missing = new java.util.ArrayList<String>();
        var stockMarkets = stockRepository.findBySymbolIn(dedup).stream()
                .collect(java.util.stream.Collectors.toMap(
                        s -> s.getSymbol(),
                        s -> s.getMarket(),
                        (a, b) -> a
                ));
        for (var symbol : dedup) {
            var series = alphaVantageClient.fetchDailyPrices(symbol, stockMarkets.get(symbol), safeLimit)
                    .stream()
                    .map(p -> new PricePoint(p.date().toString(), p.close()))
                    .toList();
            if (series.isEmpty()) {
                missing.add(symbol);
            } else {
                map.put(symbol, series);
            }
        }

        return map.entrySet().stream()
                .map(e -> new SparkSeries(e.getKey(), e.getValue()))
                .toList();
    }

    public record StockItem(Long id, String symbol, String name, String market, String tag) {}
    public record PricePoint(String date, java.math.BigDecimal close) {}
    public record PriceEvent(Long id, String title, String type, String startDate, String endDate) {}
    public record SparkSeries(String symbol, List<PricePoint> prices) {}
    public record StockDetail(
            StockItem stock,
            List<PricePoint> prices,
            List<PriceEvent> events,
            List<KeywordInfo> keywords,
            List<ArticleInfo> articles,
            List<EventArticleInfo> eventArticles,
            ForecastInfo forecast
    ) {}

    private static List<PriceEvent> alignEventsToSeries(List<PriceEvent> events, List<PricePoint> prices) {
        if (events == null || events.isEmpty() || prices == null || prices.isEmpty()) {
            return events == null ? List.of() : events;
        }
        var seriesDates = prices.stream()
                .map(p -> java.time.LocalDate.parse(p.date()))
                .toList();
        var minDate = seriesDates.get(0);
        var maxDate = seriesDates.get(seriesDates.size() - 1);
        int n = seriesDates.size();
        int m = events.size();

        return java.util.stream.IntStream.range(0, m)
                .mapToObj(i -> {
                    var evt = events.get(i);
                    try {
                        var start = java.time.LocalDate.parse(evt.startDate());
                        var end = java.time.LocalDate.parse(evt.endDate());
                        boolean inRange = !start.isBefore(minDate) && !end.isAfter(maxDate);
                        if (inRange) {
                            return new PriceEvent(
                                    evt.id(),
                                    evt.title(),
                                    evt.type(),
                                    start.toString(),
                                    end.isBefore(start) ? start.toString() : end.toString()
                            );
                        }
                    } catch (RuntimeException ex) {
                        // fall through to re-map by index
                    }
                    int anchor = Math.max(1, Math.min(n - 2, ((i + 1) * (n - 2)) / (m + 1)));
                    int endIdx = Math.min(n - 1, anchor + 2);
                    return new PriceEvent(
                            evt.id(),
                            evt.title(),
                            evt.type(),
                            seriesDates.get(anchor).toString(),
                            seriesDates.get(endIdx).toString()
                    );
                })
                .toList();
    }

}
