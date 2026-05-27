package com.example.board.service;

import com.example.board.domain.CommentEntity;
import com.example.board.dto.comment.CommentCreateRequest;
import com.example.board.dto.comment.CommentResponse;
import com.example.board.dto.comment.CommentUpdateRequest;
import com.example.board.exception.ClientErrorException;
import com.example.board.exception.comment.CommentNotFoundException;
import com.example.board.exception.post.PostNotFoundException;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }


    public CommentResponse getCommentById(Long id) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        return CommentResponse.from(comment);
    }

    public List<CommentResponse> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException(postId);
        }

        List<CommentEntity> comments = commentRepository.findAllByPostId(postId);

        return comments.stream().map(CommentResponse::from).toList();
    }

    public void createComment(CommentCreateRequest request, Long userId) {
        if (!postRepository.existsById(request.postId())) {
            throw new PostNotFoundException(request.postId());
        }

        if (request.parentId() != null) {
            if (!commentRepository.existsById(request.parentId())) {
                throw new CommentNotFoundException(request.parentId());
            }
        }

        commentRepository.save(request, userId);
    }

    public void updateComment(CommentUpdateRequest request, Long userId) {
        var comment = getCommentById(request.id());

        if (!comment.creatorId().equals(userId)) {
            throw new ClientErrorException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        commentRepository.update(request);
    }

    public void deleteComment(Long id, Long userId) {
        var comment = getCommentById(id);

        if (!comment.creatorId().equals(userId)) {
            throw new ClientErrorException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        commentRepository.delete(id);
    }
}
