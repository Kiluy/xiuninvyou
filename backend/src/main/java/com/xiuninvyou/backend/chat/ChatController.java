package com.xiuninvyou.backend.chat;

import com.xiuninvyou.backend.model.ChatMessage;
import com.xiuninvyou.backend.model.ChatSession;
import com.xiuninvyou.backend.repo.ChatMessageRepo;
import com.xiuninvyou.backend.repo.ChatSessionRepo;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatSessionRepo chatSessionRepo;
    private final ChatMessageRepo chatMessageRepo;

    public ChatController(ChatSessionRepo chatSessionRepo, ChatMessageRepo chatMessageRepo) {
        this.chatSessionRepo = chatSessionRepo;
        this.chatMessageRepo = chatMessageRepo;
    }

    record CreateSessionRequest(@NotBlank String title) {}
    record SendMessageRequest(Long sessionId, @NotBlank String content) {}

    @PostMapping("/sessions")
    public ChatSession createSession(@RequestBody CreateSessionRequest request) {
        ChatSession s = new ChatSession();
        s.setTitle(request.title());
        return chatSessionRepo.save(s);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public List<ChatMessage> listMessages(@PathVariable Long sessionId) {
        return chatMessageRepo.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamReply(@RequestBody SendMessageRequest request) {
        ChatSession session = chatSessionRepo.findById(request.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("session not found"));

        ChatMessage userMessage = new ChatMessage();
        userMessage.setSession(session);
        userMessage.setRole("user");
        userMessage.setContent(request.content());
        userMessage.setCreatedAt(Instant.now());
        chatMessageRepo.save(userMessage);

        SseEmitter emitter = new SseEmitter(0L);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                String mock = "我收到了：" + request.content() + "。这是第一版落地实现，后续可接入真实大模型。";
                String[] tokens = mock.split("");
                StringBuilder full = new StringBuilder();
                for (String t : tokens) {
                    full.append(t);
                    emitter.send(SseEmitter.event().name("delta").data(Map.of("text", t)));
                    Thread.sleep(25);
                }

                ChatMessage assistantMessage = new ChatMessage();
                assistantMessage.setSession(session);
                assistantMessage.setRole("assistant");
                assistantMessage.setContent(full.toString());
                assistantMessage.setCreatedAt(Instant.now());
                chatMessageRepo.save(assistantMessage);

                emitter.send(SseEmitter.event().name("done").data(Map.of("text", full.toString())));
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}
