package com.xiuninvyou.backend.profile;

import com.xiuninvyou.backend.security.UserContext;
import com.xiuninvyou.backend.model.AiProfile;
import com.xiuninvyou.backend.repo.AiProfileRepo;
import org.springframework.web.bind.annotation.*;

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
        payload.setId(id);
        payload.setUserId(userId);
        return repo.save(payload);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
