package com.tevind.whispr.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.dto.responses.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String errorMessage = (String) request.getAttribute("error.message");

        if (errorMessage == null) {
            errorMessage = authException.getMessage();
        }

        ErrorResponseDto errorResponseDto = DtoConverter.toErrorResponse(
                errorMessage,
                request.getRequestURI(),
                HttpStatus.UNAUTHORIZED.value());

        log.error("Authentication error: {}", errorMessage);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(), errorResponseDto);
    }
}
