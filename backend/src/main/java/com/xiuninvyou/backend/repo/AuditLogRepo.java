package com.xiuninvyou.backend.repo;

import com.xiuninvyou.backend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepo extends JpaRepository<AuditLog, Long> {
}
