package com.stocklens.content.infra;

import com.stocklens.content.domain.KeywordEntity;
import com.stocklens.content.domain.StockForecastKeywordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockForecastKeywordRepository extends JpaRepository<StockForecastKeywordEntity, Long> {

    @Query("""
            select k.word as word
            from StockForecastKeywordEntity fk
            join KeywordEntity k on k.id = fk.keywordId
            where fk.stockId = :stockId
            order by fk.position
            """)
    List<KeywordView> findKeywordViewsByStockId(@Param("stockId") Long stockId);

    interface KeywordView {
        String getWord();
    }
}
