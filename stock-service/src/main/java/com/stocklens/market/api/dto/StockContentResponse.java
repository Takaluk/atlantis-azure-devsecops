package com.stocklens.market.api.dto;

import java.util.List;

public record StockContentResponse(
        List<KeywordInfo> keywords,
        List<ArticleInfo> articles,
        List<EventArticleInfo> eventArticles,
        List<PriceEventInfo> events,
        ForecastInfo forecast
) {}
