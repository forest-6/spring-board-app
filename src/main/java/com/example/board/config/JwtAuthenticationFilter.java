package com.example.board.config;

import com.example.board.service.JwtService;
import com.example.board.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean isPostAllowed = "POST".equalsIgnoreCase(method) && (
                path.matches("^/api/[^/]+/users/signup$") ||
                        path.matches("^/api/[^/]+/users/signin$") ||
                        path.matches("^/api/[^/]+/users/refresh-token$")
        );

        boolean isGetAllowed = "GET".equalsIgnoreCase(method) && path.startsWith("^/api/[^/]+/files/");

        return isPostAllowed || isGetAllowed;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String BEARER_PREFIX = "Bearer ";
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        var securityContext = SecurityContextHolder.getContext();


        if (!ObjectUtils.isEmpty(authorization)
                && authorization.startsWith(BEARER_PREFIX)
                && securityContext.getAuthentication() == null) {
            var accessToken = authorization.substring(BEARER_PREFIX.length()).trim();
            var username = jwtService.getUsername(accessToken);
            var userEntity = userService.loadUserByUsername(username);

            var authenticationToken =
                    new UsernamePasswordAuthenticationToken(userEntity, null, userEntity.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
        }

        filterChain.doFilter(request, response);
    }
}
