package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ClientErrorException {
    public UserAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다.");
    }

    public UserAlreadyExistsException(String username) {
        super(HttpStatus.CONFLICT, "User with username " + username + " already exists.");
    }
}
