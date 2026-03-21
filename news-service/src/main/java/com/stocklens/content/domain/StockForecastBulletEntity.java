package com.stocklens.content.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(schema = "content", name = "stock_forecast_bullet")
public class StockForecastBulletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "forecast_id", nullable = false)
    private Long forecastId;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private String text;
}
