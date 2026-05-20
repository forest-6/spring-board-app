package com.example.todo.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenCacheRepository {
    private final RedisTemplate<String, String> tokenRedisTemplate;

    private String getRedisKey(String username) {
        return "user:" + username;
    }

    public void setTokenCache(String username, String refreshToken, Duration duration) {
        var redisKey = getRedisKey(username);
        tokenRedisTemplate.opsForValue().set(redisKey, refreshToken, duration);
    }

    public Optional<String> getTokenCache(String username) {
        var redisKey = getRedisKey(username);
        var refreshToken = tokenRedisTemplate.opsForValue().get(redisKey);
        return Optional.ofNullable(refreshToken);
    }

    public void deleteTokenCache(String username) {
        var redisKey = getRedisKey(username);
        tokenRedisTemplate.delete(redisKey);
    }
}
