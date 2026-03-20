package com.xiuninvyou.backend.repo;

import com.xiuninvyou.backend.model.AiProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiProfileRepo extends JpaRepository<AiProfile, Long> {
    List<AiProfile> findByUserId(Long userId);
    Optional<AiProfile> findByIdAndUserId(Long id, Long userId);
}
