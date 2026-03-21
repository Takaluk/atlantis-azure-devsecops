package com.stocklens.content.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table(schema = "content", name = "article")
public class ArticleEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 100)
    private String meta;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(name="published_at", nullable = false)
    private LocalDate publishedAt;
}
