package com.stocklens.user.api;

import com.stocklens.user.infra.WatchlistRepository;
import com.stocklens.user.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistRepository watchlistRepository;

    @GetMapping
    public List<Long> list(@AuthenticationPrincipal UserPrincipal principal) {
        return watchlistRepository.findStockIds(principal.userId());
    }

    @GetMapping("/exists")
    public boolean exists(
            @RequestParam Long stockId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return watchlistRepository.existsByUserIdAndStockId(principal.userId(), stockId);
    }

    @PostMapping("/{stockId}")
    public void add(
            @PathVariable Long stockId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (watchlistRepository.existsByUserIdAndStockId(principal.userId(), stockId)) {
            return;
        }
        var entity = new com.stocklens.user.domain.WatchlistEntity();
        entity.setUserId(principal.userId());
        entity.setStockId(stockId);
        watchlistRepository.save(entity);
    }

    @DeleteMapping("/{stockId}")
    @Transactional
    public void remove(
            @PathVariable Long stockId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        watchlistRepository.deleteByUserIdAndStockId(principal.userId(), stockId);
    }

}
