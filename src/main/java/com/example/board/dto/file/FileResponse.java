package com.example.board.dto.file;

import com.example.board.domain.FileEntity;

public record FileResponse(
        Long id,
        String originName,
        Long fileSize,
        String fileUrl
) {
    public static FileResponse from(FileEntity entity) {
        String generatedUrl = "/api/v1/files/" + entity.getStored_name();

        return new FileResponse(
                entity.getId(),
                entity.getOrigin_name(),
                entity.getFile_size(),
                generatedUrl
        );
    }
}
