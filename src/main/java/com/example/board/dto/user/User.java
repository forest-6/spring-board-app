package com.example.board.dto.user;

import com.example.board.domain.UserEntity;

public record User(
        Long id,
        String username,
        String createdAt
) {

    public static User from(UserEntity userEntity){
        return new User(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getCreated_at()
        );
    }
}
