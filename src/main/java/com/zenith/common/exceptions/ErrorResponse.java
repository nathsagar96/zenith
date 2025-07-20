package com.zenith.common.exceptions;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    String code, String message, Instant timestamp, Map<String, String> details) {
  public ErrorResponse(String code, String message, Instant timestamp) {
    this(code, message, timestamp, Map.of());
  }
}
