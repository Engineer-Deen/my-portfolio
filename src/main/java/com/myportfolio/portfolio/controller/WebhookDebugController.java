package com.myportfolio.portfolio.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Enumeration;

@RestController
@RequestMapping("/api/support")
public class WebhookDebugController {

    @PostMapping("/webhook")
    public String receiveWebhook(HttpServletRequest request) throws Exception {
        System.out.println("===== INCOMING WEBHOOK =====");

        Enumeration<String> headerNames = request.getHeaderNames() != null
                ? request.getHeaderNames()
                : Collections.emptyEnumeration();

        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println(name + ": " + request.getHeader(name));
        }

        String body = new String(request.getInputStream().readAllBytes());
        System.out.println("BODY: " + body);
        System.out.println("=============================");

        return "received";
    }
}