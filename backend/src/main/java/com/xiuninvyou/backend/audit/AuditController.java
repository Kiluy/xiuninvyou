package com.xiuninvyou.backend.audit;

import com.xiuninvyou.backend.model.AuditLog;
import com.xiuninvyou.backend.repo.AuditLogRepo;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditLogRepo repo;

    public AuditController(AuditLogRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<AuditLog> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "100") Integer limit
    ) {
        int n = Math.max(1, Math.min(500, limit));
        return repo.findTop500ByOrderByCreatedAtDesc().stream()
                .filter(x -> status == null || x.getStatus().equals(status))
                .filter(x -> userId == null || (x.getUserId() != null && x.getUserId().equals(userId)))
                .filter(x -> from == null || !x.getCreatedAt().isBefore(from))
                .filter(x -> to == null || !x.getCreatedAt().isAfter(to))
                .limit(n)
                .toList();
    }
}
