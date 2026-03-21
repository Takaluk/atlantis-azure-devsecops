package com.stocklens.market.api.dto;

public record PriceEventInfo(
        Long id,
        String title,
        String type,
        String startDate,
        String endDate
) {}
