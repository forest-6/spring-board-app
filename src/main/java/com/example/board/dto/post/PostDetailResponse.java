package com.example.board.dto.post;

import com.example.board.domain.PostEntity;
import com.example.board.dto.file.FileResponse;

import java.util.List;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        Long creatorId,
        String createdAt,
        String updatedAt,
        List<FileResponse> files) {
    public static PostDetailResponse from(PostEntity postEntity, List<FileResponse> files) {
        return new PostDetailResponse(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.getCreator_id(),
                postEntity.getCreated_at(),
                postEntity.getUpdated_at(),
                files
        );
    }
}
