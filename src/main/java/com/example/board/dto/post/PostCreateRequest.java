package com.example.board.dto.post;

public record PostCreateRequest(
        String title,
        String content,
        Long creatorId
) {
}