package com.tevind.whispr.security.jwt;

import com.tevind.whispr.exception.AuthenticationErrorException;
import com.tevind.whispr.exception.JwtTokenErrorException;
import com.tevind.whispr.security.CustomUserDetailsService;
import com.tevind.whispr.security.session.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final SessionService sessionService;

    public JwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, SessionService sessionService) {
        log.debug("Authfilter being constructed");
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.sessionService = sessionService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/profile/register") ||
                path.startsWith("/api/v1/auth/logout") ||
                path.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.debug("Retrieving token from request");
            String token = jwtUtil.getTokenFromRequest(request);
            log.debug("Request to: {} with token: {}", request.getRequestURI(), token != null ? "present" : "null");
            String requestPath = request.getRequestURI();

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Processing authentication for token on path: {}", requestPath);

                Boolean isValid = isValidSession(token);
                log.debug("Session validation result: {} for path: {}", isValid, requestPath);

                if (!Boolean.TRUE.equals(isValid)) {
                    log.warn("Session invalid for token on path: {}", requestPath);
                    throw new JwtTokenErrorException("Invalid or expired session, please login");
                }

                String username = jwtUtil.getUsername(token);
                log.debug("JWT validation starting for user: {} on path: {}", username, requestPath);

                if (username == null) {
                    throw new JwtTokenErrorException("Invalid token: username not found");
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!jwtUtil.validateToken(token, username)) {
                    throw new JwtTokenErrorException("Invalid token: Username mismatch");
                }

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("Authentication set for user: {} on path: {}", username, requestPath);
            } else if (token == null) {
                log.debug("Token is null");
                throw new JwtTokenErrorException("Please login");
            }

            filterChain.doFilter(request, response);

        } catch (JwtTokenErrorException | AuthenticationErrorException err) {
            log.error("Authentication error: {}", err.getMessage());
            request.setAttribute("error.message", err.getMessage());
            request.setAttribute("error.status", HttpStatus.UNAUTHORIZED.value());
            request.setAttribute("error.path", request.getRequestURI());
            throw err;

        } catch (Exception err) {
            log.error("Unexpected error during authentication: {}", err.getMessage());
            request.setAttribute("error.message", "An unexpected error occurred during authentication");
            request.setAttribute("error.status", 500);
            request.setAttribute("error.path", request.getRequestURI());
            throw new AuthenticationErrorException("Failed to set user authentication");
        }
    }

    private Boolean isValidSession(String token) {
        Boolean isValid = sessionService.sessionExistByToken(token);

        if (Boolean.TRUE.equals(isValid)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

}
