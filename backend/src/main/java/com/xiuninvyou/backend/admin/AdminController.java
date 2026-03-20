package com.xiuninvyou.backend.admin;

import com.xiuninvyou.backend.model.SystemConfig;
import com.xiuninvyou.backend.repo.SystemConfigRepo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/config")
public class AdminController {
    private final SystemConfigRepo systemConfigRepo;

    public AdminController(SystemConfigRepo systemConfigRepo) {
        this.systemConfigRepo = systemConfigRepo;
    }

    @GetMapping
    public SystemConfig getConfig() {
        return systemConfigRepo.findById(1L).orElseGet(() -> systemConfigRepo.save(new SystemConfig()));
    }

    @PutMapping
    public SystemConfig updateConfig(@RequestBody SystemConfig payload) {
        payload.setId(1L);
        return systemConfigRepo.save(payload);
    }
}
