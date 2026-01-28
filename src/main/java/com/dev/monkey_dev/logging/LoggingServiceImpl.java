package com.dev.monkey_dev.logging;

import com.dev.monkey_dev.common.serialization.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Base implementation for HTTP request/response logging.
 * Provides structured logging with proper formatting and header filtering.
 */
@Component
@Slf4j
public class LoggingServiceImpl implements ILoggingService {
    private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            map.put(key, value);
        }
        return map;
    }

    private Map<String, String> buildHeadersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

        // Headers to exclude (device-related and unnecessary headers)
        Set<String> excludedHeaders = Set.of(
                "sec-fetch-mode", "sec-fetch-site", "sec-fetch-dest", "sec-ch-ua",
                "sec-ch-ua-mobile", "sec-ch-ua-platform", "user-agent",
                "accept-encoding", "accept-language", "referer", "origin");

        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            if (!excludedHeaders.contains(key.toLowerCase())) {
                String value = httpServletRequest.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }

    public Map<String, String> buildHeadersMap(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String header : headerNames) {
            map.put(header, response.getHeader(header));
        }
        return map;
    }

    @Override
    public String handleLoggingRequest(HttpServletRequest httpServletRequest, Object body) {
        StringBuilder builder = new StringBuilder();
        Map<String, String> parameters = buildParametersMap(httpServletRequest);
        Map<String, String> headers = buildHeadersMap(httpServletRequest);

        builder.append("\n========== REQUEST ==========")
                .append("\nMethod: ").append(httpServletRequest.getMethod())
                .append("\nURL: ").append(httpServletRequest.getRequestURI());

        String queryString = httpServletRequest.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            builder.append("?").append(queryString);
        }

        if (!headers.isEmpty()) {
            builder.append("\nHeaders: ").append(headers);
        }

        if (!parameters.isEmpty()) {
            builder.append("\nParameters: ").append(parameters);
        }

        if (body != null) {
            builder.append("\nBody: ")
                    .append(JsonUtils.writerWithDefaultPrettyPrinter(body));
        }

        builder.append("\n=============================\n");
        return builder.toString();
    }

    @Override
    public String handleLoggingResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Object body) {
        StringBuilder builder = new StringBuilder();
        Map<String, String> responseHeaders = buildHeadersMap(httpServletResponse);
        int statusCode = httpServletResponse.getStatus();

        builder.append("\n========== RESPONSE ==========")
                .append("\nMethod: ").append(httpServletRequest.getMethod())
                .append("\nURI: ").append(httpServletRequest.getRequestURI())
                .append("\nStatus: ").append(statusCode);

        if (!responseHeaders.isEmpty()) {
            builder.append("\nHeaders: ").append(responseHeaders);
        }

        if (body != null) {
            builder.append("\nBody: ")
                    .append(JsonUtils.writeValueAsSingleLineString(body));
        }

        builder.append("\n=============================\n");
        return builder.toString();
    }
}
