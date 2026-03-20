package com.xiuninvyou.backend.repo;

import com.xiuninvyou.backend.model.MemoryVault;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoryVaultRepo extends JpaRepository<MemoryVault, Long> {
    List<MemoryVault> findByUserIdOrderByCreatedAtDesc(Long userId);
}
