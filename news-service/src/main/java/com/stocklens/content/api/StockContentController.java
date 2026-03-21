package com.stocklens.content.api;

import com.stocklens.content.infra.EventArticleRepository;
import com.stocklens.content.infra.PriceEventRepository;
import com.stocklens.content.infra.StockArticleRepository;
import com.stocklens.content.infra.StockForecastBulletRepository;
import com.stocklens.content.infra.StockForecastKeywordRepository;
import com.stocklens.content.infra.StockForecastRepository;
import com.stocklens.content.infra.StockKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockContentController {

    private final StockKeywordRepository stockKeywordRepository;
    private final StockArticleRepository stockArticleRepository;
    private final EventArticleRepository eventArticleRepository;
    private final PriceEventRepository priceEventRepository;
    private final StockForecastRepository stockForecastRepository;
    private final StockForecastBulletRepository stockForecastBulletRepository;
    private final StockForecastKeywordRepository stockForecastKeywordRepository;

    @GetMapping("/{stockId}/content")
    public StockContentResponse content(@PathVariable Long stockId) {
        var keywords = stockKeywordRepository.findKeywordViewsByStockId(stockId)
                .stream()
                .map(k -> new KeywordInfo(k.getWord(), k.getDescription(), k.getScore()))
                .toList();

        var articles = stockArticleRepository.findArticleViewsByStockId(stockId)
                .stream()
                .map(a -> new ArticleInfo(
                        a.getTitle(),
                        a.getMeta(),
                        a.getDescription(),
                        a.getPublishedAt().toString()
                ))
                .toList();

        var eventArticles = eventArticleRepository.findEventArticleViewsByStockId(stockId)
                .stream()
                .map(a -> new EventArticleInfo(
                        a.getEventId(),
                        a.getTitle(),
                        a.getMeta(),
                        a.getDescription(),
                        a.getPublishedAt().toString()
                ))
                .toList();

        var events = priceEventRepository.findByStockIdOrderByStartDateAsc(stockId)
                .stream()
                .map(e -> new PriceEventInfo(
                        e.getId(),
                        e.getTitle(),
                        e.getType(),
                        e.getStartDate().toString(),
                        e.getEndDate().toString()
                ))
                .toList();

        ForecastInfo forecast = buildForecast(stockId);

        return new StockContentResponse(keywords, articles, eventArticles, events, forecast);
    }

    public record StockContentResponse(
            List<KeywordInfo> keywords,
            List<ArticleInfo> articles,
            List<EventArticleInfo> eventArticles,
            List<PriceEventInfo> events,
            ForecastInfo forecast
    ) {}

    private ForecastInfo buildForecast(Long stockId) {
        var rows = stockForecastRepository.findByStockIdOrdered(stockId);
        if (rows.isEmpty()) {
            return null;
        }
        var forecastIds = rows.stream().map(r -> r.getId()).toList();
        var bullets = stockForecastBulletRepository.findByForecastIdInOrderByForecastIdAscPositionAsc(forecastIds);
        var bulletMap = bullets.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        b -> b.getForecastId(),
                        java.util.stream.Collectors.mapping(b -> b.getText(), java.util.stream.Collectors.toList())
                ));
        var recentKeywords = stockForecastKeywordRepository.findKeywordViewsByStockId(stockId)
                .stream()
                .map(k -> k.getWord())
                .toList();

        ForecastBlock shortTerm = null;
        ForecastBlock midTerm = null;
        ForecastBlock longTerm = null;
        for (var row : rows) {
            var block = new ForecastBlock(
                    row.getDirection(),
                    row.getConfidence(),
                    bulletMap.getOrDefault(row.getId(), java.util.List.of())
            );
            switch (row.getHorizon()) {
                case "SHORT" -> shortTerm = block;
                case "MID" -> midTerm = block;
                case "LONG" -> longTerm = block;
                default -> {
                }
            }
        }

        String model = rows.get(0).getModel();
        return new ForecastInfo(model, recentKeywords, shortTerm, midTerm, longTerm);
    }

    public record KeywordInfo(String word, String description, Integer score) {}

    public record ArticleInfo(String title, String meta, String description, String publishedAt) {}

    public record EventArticleInfo(Long eventId, String title, String meta, String description, String publishedAt) {}

    public record PriceEventInfo(Long id, String title, String type, String startDate, String endDate) {}

    public record ForecastInfo(
            String model,
            List<String> recentKeywords,
            ForecastBlock shortTerm,
            ForecastBlock midTerm,
            ForecastBlock longTerm
    ) {}

    public record ForecastBlock(
            String direction,
            Integer confidence,
            List<String> bullets
    ) {}
}
