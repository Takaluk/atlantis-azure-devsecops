package com.stocklens.content.infra;

import com.stocklens.content.domain.EventArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventArticleRepository extends JpaRepository<EventArticleEntity, Long> {

    @Query(value = """
        select ea.event_id as eventId,
               a.title as title,
               a.meta as meta,
               a.description as description,
               a.published_at as publishedAt
        from content.event_article ea
        join content.article a on a.id = ea.article_id
        where ea.stock_id = :stockId
        order by ea.event_id desc, a.published_at desc
        """, nativeQuery = true)
    List<EventArticleView> findEventArticleViewsByStockId(@Param("stockId") Long stockId);

    interface EventArticleView {
        Long getEventId();
        String getTitle();
        String getMeta();
        String getDescription();
        LocalDate getPublishedAt();
    }
}
