package com.xiuninvyou.backend.schedule;

import com.xiuninvyou.backend.model.ChatMessage;
import com.xiuninvyou.backend.model.ChatSession;
import com.xiuninvyou.backend.repo.ChatMessageRepo;
import com.xiuninvyou.backend.repo.ChatSessionRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class ProactiveMessageScheduler {
    private final ChatSessionRepo sessionRepo;
    private final ChatMessageRepo messageRepo;

    public ProactiveMessageScheduler(ChatSessionRepo sessionRepo, ChatMessageRepo messageRepo) {
        this.sessionRepo = sessionRepo;
        this.messageRepo = messageRepo;
    }

    @Scheduled(fixedDelay = 300000)
    public void proactiveGreeting() {
        List<ChatSession> sessions = sessionRepo.findAll();
        Instant now = Instant.now();
        for (ChatSession s : sessions) {
            if (Duration.between(s.getUpdatedAt(), now).toMinutes() < 30) continue;

            ChatMessage msg = new ChatMessage();
            msg.setSession(s);
            msg.setRole("assistant");
            msg.setContent("你已经有一会儿没说话了，我在这等你～");
            msg.setCreatedAt(now);
            messageRepo.save(msg);

            s.setUpdatedAt(now);
            sessionRepo.save(s);
        }
    }
}
