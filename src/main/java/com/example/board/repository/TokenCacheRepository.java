package com.example.board.repository;

import com.example.board.domain.RefreshTokenCacheEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenCacheRepository extends CrudRepository<RefreshTokenCacheEntity, String> {
    List<RefreshTokenCacheEntity> findByUsername(String username);
}
