package com.bank.bankingApp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

import static com.bank.bankingApp.util.Constant.*;

@Slf4j
@RestControllerAdvice
public class CentralizedErrorHandler {

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFoundException(final RuntimeException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return new ApiError(List.of(e.toString()),
                e.getMessage(),
                MESSAGE_REASON_ERROR_NOT_FOUND,
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
    }

    @ExceptionHandler({ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUserConflictException(final RuntimeException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        return new ApiError(List.of(e.toString()),
                e.getMessage(),
                MESSAGE_REASON_DB_CONSTRAINT_VIOLATION,
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
    }

    @ExceptionHandler(ValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleUserValidException(final RuntimeException e) {
        log.debug("Получен статус 400 Valid {}", e.getMessage(), e);
        return new ApiError(List.of(e.toString()),
                e.getMessage(),
                MESSAGE_DATE_NOT_VALID,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleUserUnauthorizedException(final RuntimeException e) {
        log.debug("Получен статус 401 Unauthorized {}", e.getMessage(), e);
        return new ApiError(
                List.of(e.toString()),
                e.getMessage(),
                MESSAGE_UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED,
                LocalDateTime.now());
    }
}