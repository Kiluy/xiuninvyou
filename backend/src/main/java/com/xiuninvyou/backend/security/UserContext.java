package com.xiuninvyou.backend.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void set(Long userId) { USER_ID.set(userId); }
    public static void clear() { USER_ID.remove(); }

    public static Long requireUserId() {
        Long id = USER_ID.get();
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        return id;
    }
}
