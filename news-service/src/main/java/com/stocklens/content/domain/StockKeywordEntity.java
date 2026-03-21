package com.stocklens.content.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(schema = "content", name = "stock_keyword")
public class StockKeywordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    @Column(name = "keyword_id", nullable = false)
    private Long keywordId;

    @Column(nullable = false)
    private Integer score;
}
