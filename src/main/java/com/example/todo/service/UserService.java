package com.example.todo.service;

import com.example.todo.domain.UserEntity;
import com.example.todo.dto.user.User;
import com.example.todo.dto.user.UserRefreshTokenResponse;
import com.example.todo.dto.user.UserTokenResponse;
import com.example.todo.exception.ClientErrorException;
import com.example.todo.exception.user.UserAlreadyExistsException;
import com.example.todo.repository.TokenCacheRepository;
import com.example.todo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenCacheRepository tokenCacheRepository;

    public UserService(UserRepository userRepository, JwtService jwtService, BCryptPasswordEncoder passwordEncoder, TokenCacheRepository tokenCacheRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.tokenCacheRepository = tokenCacheRepository;
    }

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

    @Override
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    public User signUp(String username, String password) {
        userRepository
                .findByUsername(username)
                .ifPresent(user -> { throw new UserAlreadyExistsException(); });

        var encodedPw = passwordEncoder.encode(password);
        var userEntity = userRepository.signUp(username, encodedPw);
        return User.from(userEntity);
    }

    public UserRefreshTokenResponse signIn(String username, String password) {
        UserDetails user = loadUserByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        tokenCacheRepository.setTokenCache(username, refreshToken, REFRESH_TOKEN_TTL);

        return new UserRefreshTokenResponse(accessToken, refreshToken);
    }

    public UserTokenResponse refreshToken(String refreshToken) {

        if (!jwtService.validateToken(refreshToken)) {
            throw new ClientErrorException(HttpStatus.UNAUTHORIZED, "만료되거나 유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtService.getUsername(refreshToken);
        UserEntity user = loadUserByUsername(username);
        String serverRefreshToken = tokenCacheRepository.getTokenCache(user.getUsername())
                .orElseThrow(()->new ClientErrorException(HttpStatus.UNAUTHORIZED, "사용할 수 없는 리프레시 토큰입니다."));

        if (serverRefreshToken == null || !serverRefreshToken.equals(refreshToken)) {
            throw new ClientErrorException(HttpStatus.UNAUTHORIZED, "사용할 수 없는 리프레시 토큰입니다.");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return new UserTokenResponse(newAccessToken);
    }

    public void logout(UserEntity user) {
        tokenCacheRepository.deleteTokenCache(user.getUsername());

    }
}
