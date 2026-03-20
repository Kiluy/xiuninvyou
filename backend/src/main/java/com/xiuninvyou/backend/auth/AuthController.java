package com.xiuninvyou.backend.auth;

import com.xiuninvyou.backend.model.AppUser;
import com.xiuninvyou.backend.repo.UserRepo;
import com.xiuninvyou.backend.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserRepo userRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody AuthRequest req) {
        if (userRepo.findByUsername(req.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
        }
        AppUser u = new AppUser();
        u.setUsername(req.username());
        u.setPasswordHash(encoder.encode(req.password()));
        u = userRepo.save(u);
        String token = jwtService.createToken(u.getId(), u.getUsername());
        return Map.of("userId", u.getId(), "username", u.getUsername(), "token", token);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody AuthRequest req) {
        AppUser u = userRepo.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号或密码错误"));
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号或密码错误");
        }
        String token = jwtService.createToken(u.getId(), u.getUsername());
        return Map.of("userId", u.getId(), "username", u.getUsername(), "token", token);
    }

    @PostMapping("/refresh")
    public Map<String, Object> refresh(@RequestBody Map<String, String> payload) {
        String token = payload.getOrDefault("token", "");
        Claims claims = jwtService.parse(token);
        Long userId = Long.parseLong(claims.getSubject());
        String username = String.valueOf(claims.get("username"));
        String newToken = jwtService.createToken(userId, username);
        return Map.of("token", newToken, "userId", userId, "username", username);
    }

    record AuthRequest(String username, String password) {}
}
