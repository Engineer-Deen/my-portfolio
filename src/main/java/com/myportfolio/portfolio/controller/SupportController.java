package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.service.ContactService;
import com.myportfolio.portfolio.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final ContactService contactService;

    @Value("${payment.url}")
    private String paymentUrl;

    public SupportController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/redirect")
    public Map<String, String> getSupportUrl(HttpServletRequest request)
            throws ExecutionException, InterruptedException {

        String ip = WebUtils.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        contactService.saveSupportClick(ip, userAgent != null ? userAgent : "");

        return Map.of("url", paymentUrl);
    }
}