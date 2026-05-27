package com.example.board.dto.post;

import com.example.board.domain.PostEntity;

public record PostResponse(
        Long rn,
        Long id,
        String title,
        String content,
        Long creatorId,
        String createdAt,
        String updatedAt) {
    public static PostResponse from(PostEntity postEntity) {
        return new PostResponse(
                postEntity.getRn(),
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.getCreator_id(),
                postEntity.getCreated_at(),
                postEntity.getUpdated_at()
        );
    }
}
