package dev.sagar.zenith.config;

import dev.sagar.zenith.domain.entities.User;
import dev.sagar.zenith.repositories.UserRepository;
import dev.sagar.zenith.security.JwtAuthenticationFilter;
import dev.sagar.zenith.security.UserDetailsServiceImpl;
import dev.sagar.zenith.services.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      AuthenticationService authenticationService) {
    return new JwtAuthenticationFilter(authenticationService);
  }

  @Bean
  public UserDetailsService userDetailsService(UserRepository userRepository) {

    String email = "user@test.com";

    userRepository
        .findByEmail(email)
        .orElseGet(
            () -> {
              User newUser =
                  User.builder()
                      .name("Test User")
                      .email(email)
                      .password(passwordEncoder().encode("password"))
                      .build();
              return userRepository.save(newUser);
            });

    return new UserDetailsServiceImpl(userRepository);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.POST, "/api/v1/auth/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/posts/drafts")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/posts/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/categories/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/tags/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }
}
