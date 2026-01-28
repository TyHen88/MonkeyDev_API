package com.dev.monkey_dev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import com.dev.monkey_dev.audit.AuditLogInterceptor;
import com.dev.monkey_dev.logging.RequestLoggingInterceptor;

import lombok.RequiredArgsConstructor;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final AuditLogInterceptor auditLogInterceptor;

    @Bean
    public HandlerExceptionResolver customHandlerExceptionResolver() { // Changed method name
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties mappings = new Properties();
        mappings.setProperty("Exception", "error/500");
        mappings.setProperty("RuntimeException", "error/500");
        resolver.setExceptionMappings(mappings);
        resolver.setDefaultErrorView("error/default");
        resolver.setExceptionAttribute("ex");
        resolver.setWarnLogCategory("monkey-dev.error");
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

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor);
        registry.addInterceptor(auditLogInterceptor);
    }

}

