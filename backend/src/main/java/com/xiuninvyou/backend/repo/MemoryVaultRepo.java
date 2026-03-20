package com.xiuninvyou.backend.repo;

import com.xiuninvyou.backend.model.MemoryVault;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryVaultRepo extends JpaRepository<MemoryVault, Long> {
}
