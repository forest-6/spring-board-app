package com.example.board.controller;

import com.example.board.domain.UserEntity;
import com.example.board.dto.comment.CommentCreateRequest;
import com.example.board.dto.comment.CommentResponse;
import com.example.board.dto.comment.CommentUpdateRequest;
import com.example.board.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        var comments = service.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<Void> createComment(
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {
        service.createComment(request, user.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateComment(
            @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {
        service.updateComment(request, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserEntity user
    ) {
        service.deleteComment(commentId, user.getId());
        return ResponseEntity.ok().build();
    }
}
