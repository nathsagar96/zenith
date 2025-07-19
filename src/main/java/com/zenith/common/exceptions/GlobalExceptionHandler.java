package com.zenith.common.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("Invalid argument: {}", e.getMessage(), e);
    return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiErrorResponse handleIllegalStateException(IllegalStateException e) {
    log.error("Invalid state: {}", e.getMessage(), e);
    return new ApiErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiErrorResponse handleBadCredentialsException(BadCredentialsException e) {
    log.error("Invalid credentials: {}", e.getMessage(), e);
    return new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials");
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
    log.error("Entity not found: {}", e.getMessage(), e);
    return new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiErrorResponse handleGenericException(Exception e) {
    log.error("An unexpected error occurred: {}", e.getMessage(), e);
    return new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
  }
}
