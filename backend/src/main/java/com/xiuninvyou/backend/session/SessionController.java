package com.xiuninvyou.backend.session;

import com.xiuninvyou.backend.model.ChatSession;
import com.xiuninvyou.backend.repo.ChatSessionRepo;
import org.springframework.web.bind.annotation.*;

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
        return repo.findAll();
    }

    @PutMapping("/{id}/title")
    public ChatSession rename(@PathVariable Long id, @RequestBody RenamePayload payload) {
        ChatSession s = repo.findById(id).orElseThrow();
        s.setTitle(payload.title());
        s.setUpdatedAt(Instant.now());
        return repo.save(s);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    record RenamePayload(String title) {}
}
