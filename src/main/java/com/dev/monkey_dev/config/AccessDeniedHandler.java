package com.dev.monkey_dev.config;

import com.dev.monkey_dev.common.api.ApiResponse;
import com.dev.monkey_dev.common.api.ApiStatus;
import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.common.serialization.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        try (ServletServerHttpResponse res = new ServletServerHttpResponse(response)) {
            res.setStatusCode(HttpStatus.FORBIDDEN);
            res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            ApiStatus apiStatus = new ApiStatus(StatusCode.FORBIDDEN);
            var apiResponse = new ApiResponse<Object>(apiStatus.getMessage(), apiStatus.getCode(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), null);
            res.getBody().write(JsonUtils.writeValueAsString(apiResponse).getBytes());
        }
    }
}
