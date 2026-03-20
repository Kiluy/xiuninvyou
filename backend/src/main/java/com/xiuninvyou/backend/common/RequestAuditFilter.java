package com.xiuninvyou.backend.common;

import com.xiuninvyou.backend.model.AuditLog;
import com.xiuninvyou.backend.repo.AuditLogRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RequestAuditFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestAuditFilter.class);
    private final AuditLogRepo auditLogRepo;
    private final SecretKey key;

    public RequestAuditFilter(
            AuditLogRepo auditLogRepo,
            @Value("${app.jwt.secret:xiuninvyou-super-secret-key-at-least-32-bytes-long}") String secret
    ) {
        this.auditLogRepo = auditLogRepo;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long cost = System.currentTimeMillis() - start;
            Long userId = resolveUserId(request.getHeader("Authorization"));
            log.info("[AUDIT] {} {} status={} user={} cost={}ms", request.getMethod(), request.getRequestURI(), response.getStatus(), userId == null ? "guest" : userId, cost);

            AuditLog audit = new AuditLog();
            audit.setMethod(request.getMethod());
            audit.setPath(request.getRequestURI());
            audit.setStatus(response.getStatus());
            audit.setUserId(userId);
            audit.setCostMs(cost);
            auditLogRepo.save(audit);
        }
    }

    private Long resolveUserId(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(authHeader.substring(7)).getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (Exception ex) {
            return null;
        }
    }
}
