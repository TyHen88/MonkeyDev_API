package com.dev.monkey_dev.logging;

import com.dev.monkey_dev.util.ObjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        {
            StringBuilder builder = new StringBuilder();
            Map<String, String> parameters = buildParametersMap(httpServletRequest);

            builder.append("\n[Request]")
                    .append("\n[Url] [").append(httpServletRequest.getMethod())
                    .append(" ").append(httpServletRequest.getRequestURI()).append("] ")
                    .append("\n[Header] [").append(buildHeadersMap(httpServletRequest)).append("] ");

            if (!parameters.isEmpty()) {
                builder.append("\n[Parameter] [").append(parameters).append("] ");
            }

            if (body != null) {
                builder.append("\n[Body] [")
                        .append(ObjectUtils.writerWithDefaultPrettyPrinter(body))
                        .append("]\n");
            }
            return builder.append("\n").toString();
        }
    }

    @Override
    public String handleLoggingResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Object body) {
        StringBuilder builder = new StringBuilder();

        builder.append("\n[Response]")
                .append("\n[Url] [").append(httpServletRequest.getMethod())
                .append(" ").append(httpServletRequest.getRequestURI()).append("] ")
                .append("\n[Header] [").append(buildHeadersMap(httpServletResponse)).append("] ");

        if (body != null) {
            builder.append("\n[Body] [")
                    .append(ObjectUtils.writeValueAsSingleLineString(body))
                    .append("]\n");
        }
        return builder.append("\n").toString();
    }
}
