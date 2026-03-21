package com.stocklens.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(schema = "usr", name = "watchlist")
public class WatchlistEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    @Setter
    private Long userId;

    @Column(name="stock_id", nullable = false)
    @Setter
    private Long stockId;
}
