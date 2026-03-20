package com.xiuninvyou.backend.memory;

import com.xiuninvyou.backend.security.UserContext;
import com.xiuninvyou.backend.model.MemoryVault;
import com.xiuninvyou.backend.repo.MemoryVaultRepo;
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
    public List<MemoryVault> list() {
        Long userId = UserContext.requireUserId();
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PostMapping
    public MemoryVault create(@RequestBody MemoryVault payload) {
        Long userId = UserContext.requireUserId();
        payload.setId(null);
        payload.setUserId(userId);
        return repo.save(payload);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
