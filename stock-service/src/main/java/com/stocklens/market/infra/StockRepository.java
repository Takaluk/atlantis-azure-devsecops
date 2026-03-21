package com.stocklens.market.infra;

import com.stocklens.market.domain.StockEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

    Optional<StockEntity> findBySymbol(String symbol);

    List<StockEntity> findBySymbolIn(List<String> symbols);

    @Query(value = """
        select *
        from market.stock
        where (:market is null or market = :market)
          and (:q is null or symbol ilike concat('%', :q, '%') or name ilike concat('%', :q, '%'))
        order by market, symbol
        limit :limit
        """, nativeQuery = true)
    List<StockEntity> search(@Param("market") String market,
                             @Param("q") String q,
                             @Param("limit") int limit);
}
