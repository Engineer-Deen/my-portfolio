package com.myportfolio.portfolio.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
    @Order(1) public class AdminAuthFilter extends OncePerRequestFilter {

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean isProtectedPath = path.startsWith("/api/projects")
                || path.startsWith("/api/skills")
                || path.startsWith("/api/research")
                || path.startsWith("/api/settings")
                || path.startsWith("/api/postings");

        boolean isWriteMethod = method.equals("POST") || method.equals("PUT") || method.equals("DELETE");

        if (isProtectedPath && isWriteMethod) {
            String suppliedPassword = request.getHeader("X-Admin-Password");

            if (suppliedPassword == null || !suppliedPassword.equals(adminPassword)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}