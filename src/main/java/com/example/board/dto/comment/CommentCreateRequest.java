package com.example.board.dto.comment;

public record CommentCreateRequest(
        Long postId,
        Long parentId,
        String content
) {}
