package com.example.board.dto.user;

public record UserAuthRequest(
        String username,
        String password
) {
}
