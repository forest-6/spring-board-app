package com.example.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CicdTestController {

    @GetMapping("/api/v1/cicd-test")
    public String test() {
        return "[2026-06-02] GitHub Actions 자동 배포 테스트";
    }
}