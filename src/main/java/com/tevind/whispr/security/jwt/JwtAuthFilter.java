package com.tevind.whispr.security.jwt;

import com.tevind.whispr.exception.AuthenticationErrorException;
import com.tevind.whispr.exception.JwtTokenErrorException;
import com.tevind.whispr.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.getTokenFromRequest(request);

        try {
            log.debug("Request to: {} with token: {}", request.getRequestURI(), token != null ? "present" : "null");
            String requestPath = request.getRequestURI();
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log .debug("Processing authentication for token on path: {}", requestPath);

                Boolean isValidToken = jwtUtil.isTokenExpired(token);
                log.debug("Token validation result: {} for path: {}", isValidToken, requestPath);

                if (Boolean.TRUE.equals(isValidToken)) {
                    String username = jwtUtil.getUsername(token);
                    log.debug("JWT validation starting for user: {} on path: {}", username, requestPath);

                    if (username != null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        if (jwtUtil.validateToken(token, username)) {
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );
                            authenticationToken.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                            log.debug("Authentication set for user: {} on path: {}", username, requestPath);
                        }
                    }
                } else {
                    log.warn("Session invalid for token on path: {}", requestPath);
                    throw new JwtTokenErrorException("Token is invalid");
                }
            }
        } catch (Exception err) {
            log.error("Cannot set user authentication: {}", err.getMessage());
            throw new AuthenticationErrorException("Failed to set user authentication");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }
}
