package com.example.board.service;

import com.example.board.domain.RefreshTokenCacheEntity;
import com.example.board.domain.UserEntity;
import com.example.board.dto.user.User;
import com.example.board.dto.user.UserRefreshTokenResponse;
import com.example.board.dto.user.UserTokenResponse;
import com.example.board.exception.ClientErrorException;
import com.example.board.exception.user.UserAlreadyExistsException;
import com.example.board.repository.TokenCacheRepository;
import com.example.board.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

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

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

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

        String sessionId = UUID.randomUUID().toString();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, sessionId);

        tokenCacheRepository.save(new RefreshTokenCacheEntity(sessionId, username, refreshToken, REFRESH_TOKEN_TTL.toSeconds()));

        return new UserRefreshTokenResponse(accessToken, refreshToken);
    }

    public UserRefreshTokenResponse refreshToken(String refreshToken) {

        if (!jwtService.validateToken(refreshToken)) {
            throw new ClientErrorException(HttpStatus.UNAUTHORIZED, "만료되거나 유효하지 않은 리프레시 토큰입니다.");
        }

        String sessionId = jwtService.getSessionId(refreshToken);
        String username = jwtService.getUsername(refreshToken);
        UserEntity user = loadUserByUsername(username);
        RefreshTokenCacheEntity serverCache = tokenCacheRepository.findById(sessionId)
                .orElseThrow(() -> new ClientErrorException(HttpStatus.UNAUTHORIZED, "사용할 수 없는 리프레시 토큰입니다."));

        if (!refreshToken.equals(serverCache.getRefreshToken())) {
            tokenCacheRepository.deleteById(sessionId);
            throw new ClientErrorException(HttpStatus.UNAUTHORIZED, "토큰 재사용이 감지되었습니다. 다시 로그인해주세요.");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user, sessionId);
        tokenCacheRepository.save(new RefreshTokenCacheEntity(sessionId, username, newRefreshToken, REFRESH_TOKEN_TTL.toSeconds()));

        return new UserRefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    public void signout(UserEntity user) {
        List<RefreshTokenCacheEntity> sessions = tokenCacheRepository.findByUsername(user.getUsername());
        tokenCacheRepository.deleteAll(sessions);
    }
}
