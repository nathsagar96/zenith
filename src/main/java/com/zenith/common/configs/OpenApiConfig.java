package com.zenith.common.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Zenith Blog Platform")
                .version("1.0.0")
                .contact(new Contact().name("Sagar Nath").url("https://github.com/nathsagar96"))
                .description("API documentation for the Zenith Blog Platform")
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
  }
}
