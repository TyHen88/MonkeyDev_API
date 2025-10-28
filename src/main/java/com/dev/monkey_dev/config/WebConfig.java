package com.dev.monkey_dev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.Properties;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public HandlerExceptionResolver customHandlerExceptionResolver() {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties mappings = new Properties();
        mappings.setProperty("Exception", "error/500");
        mappings.setProperty("RuntimeException", "error/500");
        resolver.setExceptionMappings(mappings);
        resolver.setDefaultErrorView("error/default");
        resolver.setExceptionAttribute("ex");
        resolver.setWarnLogCategory("monkey_dev.error");
        return resolver;
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

}
