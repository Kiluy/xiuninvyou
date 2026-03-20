package com.xiuninvyou.backend.chat;

import com.xiuninvyou.backend.llm.LlmService;
import com.xiuninvyou.backend.model.ChatMessage;
import com.xiuninvyou.backend.model.ChatSession;
import com.xiuninvyou.backend.model.GeneratedAsset;
import com.xiuninvyou.backend.model.SystemConfig;
import com.xiuninvyou.backend.repo.ChatMessageRepo;
import com.xiuninvyou.backend.repo.ChatSessionRepo;
import com.xiuninvyou.backend.repo.GeneratedAssetRepo;
import com.xiuninvyou.backend.repo.SystemConfigRepo;
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
    private final SystemConfigRepo systemConfigRepo;
    private final GeneratedAssetRepo generatedAssetRepo;
    private final LlmService llmService;

    public ChatController(ChatSessionRepo chatSessionRepo, ChatMessageRepo chatMessageRepo,
                          SystemConfigRepo systemConfigRepo, GeneratedAssetRepo generatedAssetRepo,
                          LlmService llmService) {
        this.chatSessionRepo = chatSessionRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.systemConfigRepo = systemConfigRepo;
        this.generatedAssetRepo = generatedAssetRepo;
        this.llmService = llmService;
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

        Instant now = Instant.now();
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSession(session);
        userMessage.setRole("user");
        userMessage.setContent(request.content());
        userMessage.setCreatedAt(now);
        chatMessageRepo.save(userMessage);

        session.setUpdatedAt(now);
        chatSessionRepo.save(session);

        maybeGenerateAsset(session.getId(), request.content());

        SseEmitter emitter = new SseEmitter(0L);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                SystemConfig cfg = systemConfigRepo.findById(1L).orElseGet(SystemConfig::new);
                String fullText = llmService.chat(cfg, request.content());

                for (String t : fullText.split("")) {
                    emitter.send(SseEmitter.event().name("delta").data(Map.of("text", t)));
                    Thread.sleep(20);
                }

                ChatMessage assistantMessage = new ChatMessage();
                assistantMessage.setSession(session);
                assistantMessage.setRole("assistant");
                assistantMessage.setContent(fullText);
                assistantMessage.setCreatedAt(Instant.now());
                chatMessageRepo.save(assistantMessage);

                session.setUpdatedAt(Instant.now());
                chatSessionRepo.save(session);

                emitter.send(SseEmitter.event().name("done").data(Map.of("text", fullText)));
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    private void maybeGenerateAsset(Long sessionId, String content) {
        String lower = content.toLowerCase();
        if (!(lower.contains("下雨") || lower.contains("海边") || lower.contains("咖啡") || lower.contains("晚安"))) {
            return;
        }
        GeneratedAsset asset = new GeneratedAsset();
        asset.setSessionId(sessionId);
        asset.setPrompt(content);
        asset.setAssetUrl("https://picsum.photos/seed/" + Math.abs(content.hashCode()) + "/720/960");
        generatedAssetRepo.save(asset);
    }
}
