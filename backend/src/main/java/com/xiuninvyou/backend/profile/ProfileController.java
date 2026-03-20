package com.xiuninvyou.backend.profile;

import com.xiuninvyou.backend.auth.UserHeader;
import com.xiuninvyou.backend.model.AiProfile;
import com.xiuninvyou.backend.repo.AiProfileRepo;
import jakarta.servlet.http.HttpServletRequest;
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
    public List<AiProfile> list(HttpServletRequest request) {
        Long userId = UserHeader.requireUserId(request);
        return repo.findByUserId(userId);
    }

    @PostMapping
    public AiProfile create(HttpServletRequest request, @RequestBody AiProfile payload) {
        Long userId = UserHeader.requireUserId(request);
        payload.setId(null);
        payload.setUserId(userId);
        return repo.save(payload);
    }

    @PutMapping("/{id}")
    public AiProfile update(HttpServletRequest request, @PathVariable Long id, @RequestBody AiProfile payload) {
        Long userId = UserHeader.requireUserId(request);
        payload.setId(id);
        payload.setUserId(userId);
        return repo.save(payload);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
