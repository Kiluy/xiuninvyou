package com.xiuninvyou.backend.profile;

import com.xiuninvyou.backend.security.UserContext;
import com.xiuninvyou.backend.model.AiProfile;
import com.xiuninvyou.backend.repo.AiProfileRepo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    private final AiProfileRepo repo;

    public ProfileController(AiProfileRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<AiProfile> list() {
        Long userId = UserContext.requireUserId();
        return repo.findByUserId(userId);
    }

    @PostMapping
    public AiProfile create(@RequestBody AiProfile payload) {
        Long userId = UserContext.requireUserId();
        payload.setId(null);
        payload.setUserId(userId);
        return repo.save(payload);
    }

    @PutMapping("/{id}")
    public AiProfile update(@PathVariable Long id, @RequestBody AiProfile payload) {
        Long userId = UserContext.requireUserId();
        repo.findByIdAndUserId(id, userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        payload.setId(id);
        payload.setUserId(userId);
        return repo.save(payload);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Long userId = UserContext.requireUserId();
        AiProfile profile = repo.findByIdAndUserId(id, userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repo.delete(profile);
    }
}
