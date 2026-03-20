package com.xiuninvyou.backend.memory;

import com.xiuninvyou.backend.auth.UserHeader;
import com.xiuninvyou.backend.model.MemoryVault;
import com.xiuninvyou.backend.repo.MemoryVaultRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memory")
public class MemoryController {
    private final MemoryVaultRepo repo;

    public MemoryController(MemoryVaultRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<MemoryVault> list(HttpServletRequest request) {
        Long userId = UserHeader.requireUserId(request);
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PostMapping
    public MemoryVault create(HttpServletRequest request, @RequestBody MemoryVault payload) {
        Long userId = UserHeader.requireUserId(request);
        payload.setId(null);
        payload.setUserId(userId);
        return repo.save(payload);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
