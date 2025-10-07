package com.dev.monkey_dev.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
// import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Monkey Dev API")
                        .description("API documentation for Monkey Dev application")
                        .version("1.0.0"));
    }

    // @Bean
    // public GroupedOpenApi publicApi() {
    // return GroupedOpenApi.builder()
    // .group("public")
    // .pathsToMatch("/api/**")
    // .build();
    // }
}
