package com.example.board.repository;

import com.example.board.dto.file.FileRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FileRepository {

    private final JdbcTemplate jdbcTemplate;

    public FileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(FileRequest file) {
        String sql = "INSERT INTO post_files (post_id, origin_name, stored_name, file_path, file_size) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                file.postId(),
                file.originName(),
                file.storedName(),
                file.filePath(),
                file.fileSize()
        );
    }
}
