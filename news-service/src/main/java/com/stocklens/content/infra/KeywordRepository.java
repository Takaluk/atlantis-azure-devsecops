package com.stocklens.content.infra;

import com.stocklens.content.domain.KeywordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<KeywordEntity, Long> {
    List<KeywordEntity> findAllByOrderByWordAsc();
}
