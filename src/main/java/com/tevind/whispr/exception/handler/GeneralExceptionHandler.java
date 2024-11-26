package com.tevind.whispr.exception.handler;

import com.tevind.whispr.dto.responses.ErrorResponseDto;
import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.exception.DuplicateAttributeException;
import com.tevind.whispr.exception.ProfileNotFoundException;
import com.tevind.whispr.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            Exception err,
            WebRequest request) {
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
                .body(buildErrorResponse(err, request, HttpStatus.NOT_FOUND)
                );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(err, request, HttpStatus.NOT_FOUND)
                );
    }

    @ExceptionHandler(DuplicateAttributeException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateAttributeException(
            Exception err,
            WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(err, request, HttpStatus.BAD_REQUEST)
                );
    }

    private String getPath(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }

    private String getErrMessage(Exception err) {
        return err.getMessage();
    }

    private String getErrName(Exception err) {
        return err.getClass().getName();
    }

    private ErrorResponseDto buildErrorResponse(Exception err, WebRequest request, HttpStatus status) {
        return DtoConverter.toErrorResponse(
                getErrMessage(err),
                getErrName(err),
                getPath(request),
                status.value()
        );
    }
}
