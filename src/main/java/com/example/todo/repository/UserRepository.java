package com.example.todo.repository;

import com.example.todo.domain.UserEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserEntity> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<UserEntity> results = jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(UserEntity.class),
                username);

        return results.stream().findFirst();
    }

    @Transactional
    public UserEntity signUp(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        jdbcTemplate.update(sql, username, password);

        return findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
