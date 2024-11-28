package com.tevind.whispr.exception.handler;

import com.tevind.whispr.dto.converter.DtoConverter;
import com.tevind.whispr.dto.responses.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponseDto> handleError(HttpServletRequest request) {
        String errorMsg = (String) request.getAttribute("error.message");
        int errorStatus = (int) request.getAttribute("error.status");
        String errorPath = (String) request.getAttribute("error.path");

        if (errorMsg == null) {
            errorMsg = "An unexpected error occurred";
            errorStatus = 500;
        }

        ErrorResponseDto errorResponse = DtoConverter.toErrorResponse(
                errorMsg,
                errorPath,
                errorStatus
        );

        return ResponseEntity
                .status(errorStatus)
                .body(errorResponse);
    }
}
