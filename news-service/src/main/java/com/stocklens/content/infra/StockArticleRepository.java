package com.stocklens.content.infra;

import com.stocklens.content.domain.StockArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockArticleRepository extends JpaRepository<StockArticleEntity, Long> {

    @Query(value = """
        select a.title as title,
               a.meta as meta,
               a.description as description,
               a.published_at as publishedAt
        from content.stock_article sa
        join content.article a on a.id = sa.article_id
        where sa.stock_id = :stockId
        order by a.published_at desc
        """, nativeQuery = true)
    List<ArticleView> findArticleViewsByStockId(@Param("stockId") Long stockId);

    interface ArticleView {
        String getTitle();
        String getMeta();
        String getDescription();
        LocalDate getPublishedAt();
    }
}
