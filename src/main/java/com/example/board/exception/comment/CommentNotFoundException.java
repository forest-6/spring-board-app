package com.example.board.exception.comment;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends ClientErrorException {

    public CommentNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Comment not found.");
    }

    public CommentNotFoundException(Long commentId) {
        super(HttpStatus.NOT_FOUND, "Comment with commentId " + commentId + " not found.");
    }
}