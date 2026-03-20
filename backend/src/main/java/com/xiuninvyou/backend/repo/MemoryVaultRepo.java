package com.xiuninvyou.backend.repo;

import com.xiuninvyou.backend.model.MemoryVault;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemoryVaultRepo extends JpaRepository<MemoryVault, Long> {
    List<MemoryVault> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<MemoryVault> findByIdAndUserId(Long id, Long userId);
}
