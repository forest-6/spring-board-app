package com.example.board.dto.comment;

import com.example.board.domain.CommentEntity;

public record CommentResponse(
         Long id,
         Long postId,
         Long parentId,
         String content,
         int deletedYn,
         Long creatorId,
         String createdAt,
         String updatedAt
) {
    public static CommentResponse from (CommentEntity commentEntity) {
        return new CommentResponse(
                commentEntity.getId(),
                commentEntity.getPost_id(),
                commentEntity.getParent_id(),
                commentEntity.getDeleted_yn() == 1 ? "" : commentEntity.getContent(),
                commentEntity.getDeleted_yn(),
                commentEntity.getCreator_id(),
                commentEntity.getCreated_at(),
                commentEntity.getUpdated_at()
        );
    }
}
