package dev.sagar.zenith.exception;

import dev.sagar.zenith.domain.dtos.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("Invalid argument: {}", e.getMessage(), e);
    return ApiErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(e.getMessage())
        .build();
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiErrorResponse handleIllegalStateException(IllegalStateException e) {
    log.error("Invalid state: {}", e.getMessage(), e);
    return ApiErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message(e.getMessage())
        .build();
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiErrorResponse handleBadCredentialsException(BadCredentialsException e) {
    log.error("Invalid credentials: {}", e.getMessage(), e);
    return ApiErrorResponse.builder()
        .status(HttpStatus.UNAUTHORIZED.value())
        .message("Invalid credentials")
        .build();
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
    log.error("Entity not found: {}", e.getMessage(), e);
    return ApiErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .message(e.getMessage())
        .build();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiErrorResponse handleGenericException(Exception e) {
    log.error("An unexpected error occurred: {}", e.getMessage(), e);
    return ApiErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message("An unexpected error occurred")
        .build();
  }
}
