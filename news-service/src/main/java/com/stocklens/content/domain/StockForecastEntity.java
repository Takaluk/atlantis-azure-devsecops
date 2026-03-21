package com.stocklens.content.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(schema = "content", name = "stock_forecast")
public class StockForecastEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    @Column(nullable = false)
    private String horizon;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private Integer confidence;

    @Column(nullable = false)
    private String model;
}
