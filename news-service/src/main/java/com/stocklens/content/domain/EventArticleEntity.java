package com.stocklens.content.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(schema = "content", name = "event_article")
public class EventArticleEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    @Column(name = "article_id", nullable = false)
    private Long articleId;
}
