package com.stocklens.frontend.api.dto;

public record StockItem(Long id, String symbol, String name, String market, String tag) {}
