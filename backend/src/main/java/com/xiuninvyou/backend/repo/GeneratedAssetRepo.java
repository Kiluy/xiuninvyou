package com.xiuninvyou.backend.repo;

import com.xiuninvyou.backend.model.GeneratedAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedAssetRepo extends JpaRepository<GeneratedAsset, Long> {
    List<GeneratedAsset> findByUserIdAndSessionIdOrderByCreatedAtDesc(Long userId, Long sessionId);
}
