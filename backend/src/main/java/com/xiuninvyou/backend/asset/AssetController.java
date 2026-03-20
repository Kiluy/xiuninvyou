package com.xiuninvyou.backend.asset;

import com.xiuninvyou.backend.security.UserContext;
import com.xiuninvyou.backend.model.GeneratedAsset;
import com.xiuninvyou.backend.repo.GeneratedAssetRepo;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private final GeneratedAssetRepo repo;

    public AssetController(GeneratedAssetRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/{sessionId}")
    public List<GeneratedAsset> list(@PathVariable Long sessionId) {
        Long userId = UserContext.requireUserId();
        return repo.findByUserIdAndSessionIdOrderByCreatedAtDesc(userId, sessionId);
    }

    @PostMapping("/generate")
    public GeneratedAsset generate(@RequestBody GenerateRequest req) {
        Long userId = UserContext.requireUserId();
        GeneratedAsset asset = new GeneratedAsset();
        asset.setUserId(userId);
        asset.setSessionId(req.sessionId());
        asset.setPrompt(req.prompt());
        asset.setCreatedAt(Instant.now());
        asset.setAssetUrl("https://picsum.photos/seed/" + Math.abs(req.prompt().hashCode()) + "/720/960");
        return repo.save(asset);
    }

    record GenerateRequest(Long sessionId, String prompt) {}
}
