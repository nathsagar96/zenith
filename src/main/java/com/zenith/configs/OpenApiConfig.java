package com.zenith.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "Zenith API",
                        version = "1.0.0",
                        description = "RESTful API for the Zenith application",
                        contact = @Contact(name = "Zenith Support", url = "https://github.com/nathsagar96"),
                        license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")),
        servers = {
            @Server(url = "http://localhost:8080", description = "Local Development"),
        },
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT authentication token")
public class OpenApiConfig {}
