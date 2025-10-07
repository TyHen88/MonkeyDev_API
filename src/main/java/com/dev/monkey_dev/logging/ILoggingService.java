package com.dev.monkey_dev.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ILoggingService {
    String handleLoggingRequest(HttpServletRequest httpServletRequest, Object body);

    String handleLoggingResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Object body);
}