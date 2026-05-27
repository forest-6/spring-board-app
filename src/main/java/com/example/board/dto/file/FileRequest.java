package com.example.board.dto.file;

public record FileRequest(
        Long postId,
        String originName,
        String storedName,
        String filePath,
        Long fileSize
) {
}
