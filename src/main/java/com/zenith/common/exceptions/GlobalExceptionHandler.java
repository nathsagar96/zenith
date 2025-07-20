package com.zenith.common.exceptions;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("Invalid argument: {}", e.getMessage(), e);
    ErrorResponse error = new ErrorResponse(
            "BAD_REQUEST",
            e.getMessage(),
            Instant.now()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
    log.error("Invalid state: {}", e.getMessage(), e);
    ErrorResponse error = new ErrorResponse(
            "CONFLICT",
            e.getMessage(),
            Instant.now()
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
    log.error("Invalid credentials: {}", e.getMessage(), e);
    ErrorResponse error = new ErrorResponse(
            "UNAUTHORIZED",
            "Invalid credentials",
            Instant.now()
    );
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
    log.error("Entity not found: {}", e.getMessage(), e);
    ErrorResponse error = new ErrorResponse(
            "NOT_FOUND",
            e.getMessage(),
            Instant.now()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
    log.error("Invalid token: {}", e.getMessage(), e);
    ErrorResponse error = new ErrorResponse(
            "UNAUTHORIZED",
            e.getMessage(),
            Instant.now()
    );
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(
          MethodArgumentNotValidException ex) {
    Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage
            ));

    ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Invalid input parameters",
            Instant.now(),
            errors
    );
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
    log.error("An unexpected error occurred: {}", e.getMessage(), e);
    ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            e.getMessage(),
            Instant.now()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
