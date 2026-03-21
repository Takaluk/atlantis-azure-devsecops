package com.stocklens.content.infra;

import com.stocklens.content.domain.StockForecastBulletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockForecastBulletRepository extends JpaRepository<StockForecastBulletEntity, Long> {
    List<StockForecastBulletEntity> findByForecastIdInOrderByForecastIdAscPositionAsc(List<Long> forecastIds);
}
