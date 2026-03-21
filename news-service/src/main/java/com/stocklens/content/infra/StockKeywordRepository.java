package com.stocklens.content.infra;

import com.stocklens.content.domain.StockKeywordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockKeywordRepository extends JpaRepository<StockKeywordEntity, Long> {

    @Query(value = """
        select k.word as word,
               k.description as description,
               sk.score as score
        from content.stock_keyword sk
        join content.keyword k on k.id = sk.keyword_id
        where sk.stock_id = :stockId
        order by sk.score desc, k.word
        """, nativeQuery = true)
    List<KeywordView> findKeywordViewsByStockId(@Param("stockId") Long stockId);

    interface KeywordView {
        String getWord();
        String getDescription();
        Integer getScore();
    }
}
