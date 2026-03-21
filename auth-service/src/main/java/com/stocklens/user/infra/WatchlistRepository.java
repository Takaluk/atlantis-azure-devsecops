package com.stocklens.user.infra;

import com.stocklens.user.domain.WatchlistEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<WatchlistEntity, Long> {

    @Query(value = "select stock_id from usr.watchlist where user_id = :userId", nativeQuery = true)
    List<Long> findStockIds(@Param("userId") Long userId);

    boolean existsByUserIdAndStockId(Long userId, Long stockId);

    void deleteByUserIdAndStockId(Long userId, Long stockId);
}
