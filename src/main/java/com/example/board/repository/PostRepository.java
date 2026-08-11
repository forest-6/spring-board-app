package com.example.board.repository;

import com.example.board.domain.FileEntity;
import com.example.board.domain.PostEntity;
import com.example.board.dto.post.PostCreateRequest;
import com.example.board.dto.post.PostSearchRequest;
import com.example.board.dto.post.PostUpdateRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM posts WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public Long save(PostCreateRequest request) {
        String sql = "INSERT INTO posts (title, content, creator_id) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.title());
            ps.setString(2, request.content());
            ps.setLong(3, request.creatorId());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<PostEntity> findAllPaged(PostSearchRequest request) {
        // rownum 방식
        // "SELECT * from " +
        // "(SELECT @rownum := @rownum + 1 as rn, id, title, content, creator_id, created_at, updated_at " +
        // "FROM posts, (SELECT @rownum := 0) as rowcolumn order by created_at DESC) " +
        // "WHERE rn > 0 AND rn <= 10;";
        String keyword = (request.searchKeyword() == null) ? "" : request.searchKeyword().toLowerCase();

        int offset = (request.pageIndex() - 1) * request.pageSize();
        int start = offset + 1;
        int end = offset + request.pageSize();

        String sql = "SELECT T.* FROM (" +
                "   SELECT ROW_NUMBER() OVER (ORDER BY created_at DESC, id DESC) AS rn, P.* " +
                "   FROM posts P" +
                "   WHERE LOWER(P.title) LIKE CONCAT('%', ?, '%')" +
                ") T " +
                "WHERE rn BETWEEN ? AND ?";

        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(PostEntity.class),
                keyword,
                start,
                end);
    }

    public int getTotalCount(String searchKeyword) {
        String keyword = (searchKeyword == null) ? "" : searchKeyword.toLowerCase();

        String sql = "SELECT COUNT(*) FROM posts" +
                " WHERE LOWER(title) LIKE CONCAT('%', ?, '%')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, keyword);
        return (count != null) ? count : 0;
    }

    public Optional<PostEntity> findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql,
                new BeanPropertyRowMapper<>(PostEntity.class),
                id));
    }

    public List<FileEntity> findFilesByPostId(Long postId) {
        String sql = "SELECT * FROM post_files WHERE post_id = ?";

        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(FileEntity.class),
                postId
        );
    }

    public void update(PostUpdateRequest request) {
        String sql = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
        jdbcTemplate.update(sql, request.title(), request.content(), request.id());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public int findViewCount(Long id) {
        String sql = "SELECT view_count FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    // 틀린 방법
    public void updateViewCount(Long id, int viewCount) {
        String sql = "UPDATE posts SET view_count = ? WHERE id = ?";
        jdbcTemplate.update(sql, viewCount, id);
    }

    // 맞는 방법
    public void increaseViewCountAtomic(Long id){
        String sql = "UPDATE posts SET view_count = view_count + 1 WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
