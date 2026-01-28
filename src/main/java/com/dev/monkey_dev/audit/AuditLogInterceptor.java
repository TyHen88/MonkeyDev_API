package com.dev.monkey_dev.audit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.dev.monkey_dev.helper.AuthHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditLogInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_ATTR = "audit.requestId";

    private final AuditLogPublisher auditLogPublisher;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getHeader("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        request.setAttribute(REQUEST_ID_ATTR, requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        if (shouldSkip(request)) {
            return;
        }

        Long actorUserId = null;
        try {
            actorUserId = AuthHelper.getUserId();
        } catch (Exception ignored) {
            // anonymous or unauthenticated
        }

        String requestId = (String) request.getAttribute(REQUEST_ID_ATTR);
        String action = request.getMethod() + " " + request.getRequestURI();
        boolean success = response.getStatus() < 400;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("status", response.getStatus());
        metadata.put("query", request.getQueryString());
        metadata.put("method", request.getMethod());

        String entityType = resolveEntityType(request.getRequestURI());
        Long entityId = resolveEntityId(request.getRequestURI());

        AuditLogEvent event = new AuditLogEvent(
                actorUserId,
                action,
                entityType,
                entityId,
                ex != null ? ex.getMessage() : null,
                requestId,
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                success,
                metadata);

        auditLogPublisher.publish(event);
    }

    private String resolveEntityType(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String cleaned = path.startsWith("/") ? path.substring(1) : path;
        String[] segments = cleaned.split("/");
        if (segments.length == 0) {
            return null;
        }
        int index = Math.min(segments.length - 1, 3);
        return segments[index];
    }

    private Long resolveEntityId(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        String cleaned = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        String[] segments = cleaned.split("/");
        if (segments.length == 0) {
            return null;
        }
        String last = segments[segments.length - 1];
        try {
            return Long.parseLong(last);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars")
                || path.startsWith("/actuator");
    }
}
