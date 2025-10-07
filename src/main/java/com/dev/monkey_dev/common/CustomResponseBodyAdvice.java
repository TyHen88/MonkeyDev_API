package com.dev.monkey_dev.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.dev.monkey_dev.logging.MonkeyDevLoggingService;

@ControllerAdvice
@RequiredArgsConstructor
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final MonkeyDevLoggingService monkeyDevLoggingService;

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest serverHttpRequest,
            @NonNull ServerHttpResponse serverHttpResponse) {
        if (serverHttpRequest instanceof ServletServerHttpRequest
                && serverHttpResponse instanceof ServletServerHttpResponse) {
            HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
            HttpServletResponse response = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();

            // Log responses for all API endpoints
            if (request.getRequestURI().startsWith("/api/")) {
                monkeyDevLoggingService.logResponse(request, response, body);
            }

        }

        return body;
    }
}
