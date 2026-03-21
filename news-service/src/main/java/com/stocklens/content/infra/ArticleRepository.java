package com.stocklens.content.infra;

import com.stocklens.content.domain.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
    List<ArticleEntity> findTop4ByOrderByPublishedAtDesc();
}
