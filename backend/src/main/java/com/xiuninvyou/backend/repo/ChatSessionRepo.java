package com.xiuninvyou.backend.repo;

import com.xiuninvyou.backend.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepo extends JpaRepository<ChatSession, Long> {
}
