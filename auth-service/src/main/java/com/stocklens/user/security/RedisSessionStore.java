package com.stocklens.user.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisSessionStore {
    private final StringRedisTemplate redisTemplate;

    public RedisSessionStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void store(String jti, Long userId, Duration ttl) {
        redisTemplate.opsForValue().set(key(jti), String.valueOf(userId), ttl);
    }

    public boolean exists(String jti) {
        Boolean hasKey = redisTemplate.hasKey(key(jti));
        return Boolean.TRUE.equals(hasKey);
    }

    public void remove(String jti) {
        redisTemplate.delete(key(jti));
    }

    private String key(String jti) {
        return "session:" + jti;
    }
}
