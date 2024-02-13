package com.simulation.fifa.common;


import com.simulation.fifa.api.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            Exception.class
    })
    protected ResponseEntity<ApiResponse<?>> internalServerErrorException(Exception exception) {
        log.error("INTERNAL_SERVER_ERROR", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.createError(exception.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ApiResponse<?>> badRequestException(RuntimeException runtimeException) {
        log.error("BAD_REQUEST", runtimeException);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError(runtimeException.getMessage()));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<ApiResponse<?>> forbiddenException(Exception exception) {
        log.error("FORBIDDEN", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.createError(exception.getMessage()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ApiResponse<?>> unauthorizedException(Exception exception) {
        log.error("UNAUTHORIZED", exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.createError(exception.getMessage()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ApiResponse<?>> notFoundException(Exception exception) {
        log.error("NOT_FOUND", exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.createError(exception.getMessage()));
    }
}