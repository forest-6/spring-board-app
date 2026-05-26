package com.example.todo.repository;

import com.example.todo.domain.RefreshTokenCacheEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenCacheRepository extends CrudRepository<RefreshTokenCacheEntity, String> {
}
