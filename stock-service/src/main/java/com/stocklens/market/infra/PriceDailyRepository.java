package com.stocklens.market.infra;

import com.stocklens.market.domain.PriceDailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceDailyRepository extends JpaRepository<PriceDailyEntity, Long> {
    List<PriceDailyEntity> findByStockIdOrderByDateAsc(Long stockId);

    @Query(value = """
        select t.symbol as symbol, t.d as date, t.close as close
        from (
            select s.symbol as symbol,
                   p.d as d,
                   p.close as close,
                   row_number() over (partition by p.stock_id order by p.d desc) as rn
            from market.price_daily p
            join market.stock s on s.id = p.stock_id
            where s.symbol in (:symbols)
        ) t
        where t.rn <= :limit
        order by t.symbol, t.d
        """, nativeQuery = true)
    List<SparkSlice> findRecentSlices(@Param("symbols") List<String> symbols,
                                      @Param("limit") int limit);

    interface SparkSlice {
        String getSymbol();
        java.time.LocalDate getDate();
        java.math.BigDecimal getClose();
    }
}
