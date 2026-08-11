package com.example.board.service;

import com.example.board.dto.common.PagingResult;
import com.example.board.domain.PostEntity;
import com.example.board.dto.file.FileRequest;
import com.example.board.dto.file.FileResponse;
import com.example.board.dto.post.*;
import com.example.board.exception.ClientErrorException;
import com.example.board.exception.post.PostNotFoundException;
import com.example.board.repository.FileRepository;
import com.example.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final FileRepository fileRepository;

    public PostService(PostRepository repository, FileRepository fileRepository) {
        this.postRepository = repository;
        this.fileRepository = fileRepository;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional(rollbackFor = Exception.class)
    public void createPost(PostCreateRequest post, List<MultipartFile> files) throws IOException {
        Long postId = postRepository.save(post);

        if (files == null || files.isEmpty()) return;

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originName = file.getOriginalFilename();
            String storedName = UUID.randomUUID() + "_" + originName;
            String fullPath = uploadDir + storedName;

            file.transferTo(new File(fullPath));

            FileRequest fileEntity = new FileRequest(postId, originName, storedName, fullPath, file.getSize());

            fileRepository.save(fileEntity);
        }
    }

    public PagingResult<PostResponse> getPostPage(PostSearchRequest request) {
        List<PostEntity> posts = postRepository.findAllPaged(request);
        int totalCount = postRepository.getTotalCount(request.searchKeyword());
        var rows = posts.stream().map(post -> PostResponse.from(post)).toList();

        return PagingResult.of(totalCount, request.pageSize(), request.pageIndex(), rows);
    }

    @Cacheable(cacheNames = "post", key = "#id", sync = true)
    public PostDetailResponse getPost(Long id) {
        PostEntity post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        List<FileResponse> files = postRepository.findFilesByPostId(id)
                .stream().map(file -> FileResponse.from(file)).toList();

        return PostDetailResponse.from(post, files);
    }

    @CacheEvict(cacheNames = "post", key = "#request.id()")
    public void updatePost(PostUpdateRequest request, Long userId) {
        var postDetail = getPost(request.id());

        if (!postDetail.creatorId().equals(userId)) {
            throw new ClientErrorException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        postRepository.update(request);
    }

    @CacheEvict(cacheNames = "post", key = "#id")
    public void deletePost(Long id, Long userId) {
        var postDetail = getPost(id);

        if (!postDetail.creatorId().equals(userId)) {
            throw new ClientErrorException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }

        postRepository.delete(id);
    }

    // 틀린 방법
    public void increaseViewCountWrong(Long id) {
        int current = postRepository.findViewCount(id);
        postRepository.updateViewCount(id, current+1);
    }

    // 맞는 방법
    public void increaseViewCountRight(Long id) {
        postRepository.increaseViewCountAtomic(id);
    }
}