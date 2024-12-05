package com.tevind.whispr.exception.handler;

import com.tevind.whispr.dto.responses.ErrorResponseDto;
import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GeneralExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            Exception err,
            WebRequest request) {
        log.error("Handling general exception", err);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(err, request, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProfileNotFoundException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(err, request, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(err, request, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(DuplicateAttributeException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateAttributeException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(err, request, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(JwtTokenErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleJwtTokenErrorException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(err, request, HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(AuthenticationErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthErrorException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(err, request, HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(ProfileErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleProfileErrorException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(err, request, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountNotActiveException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse(err, request, HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(SessionErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleSessionErrorException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(err, request, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ThreadErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleThreadErrorException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(err, request, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ThreadNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleThreadNotFoundException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(err, request, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MessageErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleMessageErrorException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(err, request, HttpStatus.BAD_REQUEST));
    }

    private String getPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }

    private String getErrMessage(Exception err) {
        return err.getMessage();
    }

    private ErrorResponseDto buildErrorResponse(Exception err, WebRequest request, HttpStatus status) {
        return DtoConverter.toErrorResponse(
                getErrMessage(err),
                getPath(request),
                status.value()
        );
    }
}
