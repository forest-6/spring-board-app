package com.example.board.service;

import com.example.board.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest   // 진짜 스프링 + 진짜 MySQL을 띄운다 (Mock 아님!)
class PostServiceConcurrencyTest {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("동시에 100번 조회수 증가 - 틀린 방법")
    void concurrentViewCount_wrong() throws InterruptedException {
        Long targetId = 1L;
        int threadCount = 100;

        // 준비: 조회수를 0으로 초기화
        postRepository.updateViewCount(targetId, 0);

        ExecutorService executor = Executors.newFixedThreadPool(32); // 스레드 32개 풀
        CountDownLatch latch = new CountDownLatch(threadCount);      // 100 카운터

        // 100개의 "조회수 +1" 작업을 던진다
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    postService.increaseViewCountRight(targetId);
                } finally {
                    latch.countDown();   // 끝날 때마다 카운터 -1
                }
            });
        }

        latch.await();        // 100개 다 끝날 때까지 대기
        executor.shutdown();

        int result = postRepository.findViewCount(targetId);
        System.out.println("======================================");
        System.out.println("기대한 조회수: " + threadCount);   // 100
        System.out.println("실제 조회수:   " + result);        // 100보다 작을 것!
        System.out.println("증발한 갱신:   " + (threadCount - result));
        System.out.println("======================================");
    }
}
