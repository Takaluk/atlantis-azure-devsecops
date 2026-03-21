package com.stocklens.market.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(schema = "market", name = "price_daily")
public class PriceDailyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="stock_id", nullable = false)
    private Long stockId;

    @Column(name="d", nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal close;
}
