package com.xiuninvyou.backend.memory;

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
        return repo.findAll();
    }

    @PostMapping
    public MemoryVault create(@RequestBody MemoryVault payload) {
        payload.setId(null);
        return repo.save(payload);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
