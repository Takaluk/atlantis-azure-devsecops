package com.stocklens.content.infra;

import com.stocklens.content.domain.StockForecastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockForecastRepository extends JpaRepository<StockForecastEntity, Long> {

    @Query("""
            select f from StockForecastEntity f
            where f.stockId = :stockId
            order by case f.horizon
                when 'SHORT' then 1
                when 'MID' then 2
                when 'LONG' then 3
                else 4
            end
            """)
    List<StockForecastEntity> findByStockIdOrdered(@Param("stockId") Long stockId);
}
