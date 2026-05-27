package com.example.board.controller;

import com.example.board.dto.common.PagingResult;
import com.example.board.domain.UserEntity;
import com.example.board.dto.post.*;
import com.example.board.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> createPost(
            @RequestPart("post") PostCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserEntity user) throws IOException {
        service.createPost(new PostCreateRequest(request.title(), request.content(), user.getId()),files);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PagingResult<PostResponse>> getPosts(
            @ModelAttribute PostSearchRequest request
    ) {
        return ResponseEntity.ok(service.getPostPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPost(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long id,
            @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {
        service.updatePost(new PostUpdateRequest(id, request.title(), request.content()), user.getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user) {
        service.deletePost(id, user.getId());

        return ResponseEntity.ok().build();
    }
}