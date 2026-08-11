package com.example.board.controller;

import com.example.board.domain.UserEntity;
import com.example.board.dto.user.*;
import com.example.board.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody UserAuthRequest request) {
        var user = service.signUp(request.username(), request.password());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserRefreshTokenResponse> signIn(@RequestBody UserAuthRequest request) {
        var response = service.signIn(request.username(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<UserRefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(service.refreshToken(request.refreshToken()));
    }

    @GetMapping("signout")
    public ResponseEntity<Void> signout(
            @AuthenticationPrincipal UserEntity user
    ) {
        service.signout(user);
        return ResponseEntity.ok().build();
    }
}
