package com.example.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash("user")
public class RefreshTokenCacheEntity {
    @Id
    private String sessionId;

    @Indexed
    private String username;

    private String refreshToken;

    @TimeToLive
    private Long ttl;
}
