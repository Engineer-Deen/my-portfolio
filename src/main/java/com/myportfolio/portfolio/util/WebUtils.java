package com.myportfolio.portfolio.util;

import jakarta.servlet.http.HttpServletRequest;

public class WebUtils {

    private WebUtils() {}

    public static String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}