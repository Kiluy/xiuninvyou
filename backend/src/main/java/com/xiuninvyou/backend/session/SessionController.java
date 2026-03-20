package com.xiuninvyou.backend.session;

import com.xiuninvyou.backend.security.UserContext;
import com.xiuninvyou.backend.model.ChatSession;
import com.xiuninvyou.backend.repo.ChatSessionRepo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final ChatSessionRepo repo;

    public SessionController(ChatSessionRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ChatSession> list() {
        Long userId = UserContext.requireUserId();
        return repo.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @PutMapping("/{id}/title")
    public ChatSession rename(@PathVariable Long id, @RequestBody RenamePayload payload) {
        Long userId = UserContext.requireUserId();
        ChatSession s = repo.findByIdAndUserId(id, userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        s.setTitle(payload.title());
        s.setUpdatedAt(Instant.now());
        return repo.save(s);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Long userId = UserContext.requireUserId();
        ChatSession s = repo.findByIdAndUserId(id, userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repo.delete(s);
    }

    record RenamePayload(String title) {}
}
