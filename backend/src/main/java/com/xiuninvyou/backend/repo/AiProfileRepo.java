package com.xiuninvyou.backend.repo;

import com.xiuninvyou.backend.model.AiProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiProfileRepo extends JpaRepository<AiProfile, Long> {
    List<AiProfile> findByUserId(Long userId);
}
