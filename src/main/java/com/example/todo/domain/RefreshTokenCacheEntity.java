package com.example.todo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@AllArgsConstructor
@RedisHash("user")
public class RefreshTokenCacheEntity {
    @Id
    private String username;

    private String refreshToken;

    @TimeToLive
    private Long ttl;
}
