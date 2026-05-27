package com.example.board.dto.post;

public record PostUpdateRequest(
        Long id,
        String title,
        String content
) {
}
