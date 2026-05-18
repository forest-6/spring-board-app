package com.example.todo.service;

import com.example.todo.domain.UserEntity;
import com.example.todo.dto.user.User;
import com.example.todo.dto.user.UserRefreshTokenResponse;
import com.example.todo.exception.user.UserAlreadyExistsException;
import com.example.todo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository repository;

    @Test
    @DisplayName("로그인 성공")
    void signIn_Success() {
        // given
        String username = "testUser";
        String password = "qwer123";
        String encodedPw = "encoded_qwer123";

        UserEntity mockUser = UserEntity.of(username, encodedPw);

        when(repository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(mockUser)).thenReturn("mock-access-token");
        when(jwtService.generateRefreshToken(mockUser)).thenReturn("mock-refresh-token");

        // when
        UserRefreshTokenResponse response = userService.signIn(username, password);

        // then
        assertNotNull(response);

        assertEquals("mock-access-token", response.accessToken());
        assertEquals("mock-refresh-token", response.refreshToken());
        verify(repository, times(1)).saveRefreshToken(username, "mock-refresh-token");
    }

    @Test
    @DisplayName("로그인 실패: 비밀번호 불일치")
    void signIn_Fail_PwNotMatch() {
        // given
        String username = "testUser";
        String password = "qwer123";
        String encodedPw = "encoded_qwer123";

        UserEntity mockUser = UserEntity.of(username, encodedPw);

        when(repository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(false);

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.signIn(username, password);
        });

        // then
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        verify(repository, never()).saveRefreshToken(any(), any());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // given
        String username = "testUser";
        String password = "qwer123!";
        String encodedPw = "encoded_qwer123!";

        UserEntity mockUser = UserEntity.of(username, encodedPw);

        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPw);
        when(repository.signUp(username, encodedPw)).thenReturn(mockUser);

        // when
        User response = userService.signUp(username, password);

        // then
        assertEquals(username, response.username());
    }

    @Test
    @DisplayName("회원가입 실패: 아이디 존재")
    void signUp_Fail_ExistsUsername() {
        // given
        String username = "testUser";
        String password = "qwer123!";
        String encodedPw = "encoded_qwer123!";

        UserEntity mockUser = UserEntity.of(username, encodedPw);

        when(repository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // when
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.signUp(username, password);
        });

        // then
        assertEquals("이미 존재하는 사용자입니다.", exception.getMessage());
        verify(repository, never()).signUp(username, encodedPw);
    }
}