package com.dev.monkey_dev.config;

import com.dev.monkey_dev.common.api.ApiResponse;
import com.dev.monkey_dev.common.api.StatusCode;
import com.dev.monkey_dev.common.serialization.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UnauthorizedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        try (ServletServerHttpResponse res = new ServletServerHttpResponse(response)) {
            res.setStatusCode(HttpStatus.UNAUTHORIZED);
            res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            ApiResponse<Object> apiResponse = ApiResponse.error(StatusCode.UNAUTHORIZED.getMessage(),
                    StatusCode.UNAUTHORIZED.getHttpStatus());

            res.getBody().write(JsonUtils.writeValueAsString(apiResponse).getBytes());
        }
    }
}
