package com.stocklens.market.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(schema = "market", name = "stock")
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String symbol;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 8)
    private String market; // US/Forex/Crypto

    @Column(nullable = false, length = 32)
    private String tag;
}
