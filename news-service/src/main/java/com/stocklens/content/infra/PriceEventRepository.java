package com.stocklens.content.infra;

import com.stocklens.content.domain.PriceEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceEventRepository extends JpaRepository<PriceEventEntity, Long> {
    List<PriceEventEntity> findByStockIdOrderByStartDateAsc(Long stockId);
}
