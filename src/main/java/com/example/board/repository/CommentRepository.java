package com.example.board.repository;

import com.example.board.domain.CommentEntity;
import com.example.board.dto.comment.CommentCreateRequest;
import com.example.board.dto.comment.CommentUpdateRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(CommentCreateRequest request, Long userId) {
        String sql = "INSERT INTO comments (post_id, parent_id, content, creator_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(
                sql,
                request.postId(),
                request.parentId(),
                request.content(),
                userId);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM comments WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }


    public List<CommentEntity> findAllByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CommentEntity.class), postId);
    }

    public Optional<CommentEntity> findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CommentEntity.class), id)
                .stream()
                .findFirst();
    }


    public void update(CommentUpdateRequest request) {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";
        jdbcTemplate.update(sql, request.content(), request.id());
    }

    public void delete(Long id) {
        String sql = "UPDATE comments SET deleted_yn=? WHERE id = ?";
        jdbcTemplate.update(sql, 1, id);
    }

}
