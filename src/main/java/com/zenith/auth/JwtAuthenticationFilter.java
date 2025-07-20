package com.zenith.auth;

import com.zenith.auth.impl.UserDetailsImpl;
import com.zenith.common.exceptions.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final AuthenticationService authenticationService;

  public JwtAuthenticationFilter(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    String token = extractToken(request);
    if (token != null && !token.isEmpty()) {
      try {
        UserDetails userDetails = authenticationService.validateToken(token);
        if (userDetails != null) {
          // Set the authentication in the security context
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authentication);

          if (userDetails instanceof UserDetailsImpl userDetailsImpl) {
            request.setAttribute("userId", userDetailsImpl.getId());
          }
        }
      } catch (SignatureException | MalformedJwtException | ExpiredJwtException e) {
        throw new InvalidTokenException("Invalid or expired JWT token", e);
      } catch (Exception e) {
        throw new InvalidTokenException("An unexpected error occurred during token validation", e);
      }
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }
}
