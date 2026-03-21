package com.stocklens.market.api.dto;

import java.util.List;

public record ForecastInfo(
        String model,
        List<String> recentKeywords,
        ForecastBlock shortTerm,
        ForecastBlock midTerm,
        ForecastBlock longTerm
) {
    public record ForecastBlock(
            String direction,
            Integer confidence,
            List<String> bullets
    ) {}
}
