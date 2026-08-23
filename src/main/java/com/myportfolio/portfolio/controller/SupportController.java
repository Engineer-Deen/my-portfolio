package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.service.ContactService;
import com.myportfolio.portfolio.service.MonimeService;
import com.myportfolio.portfolio.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final ContactService contactService;
    private final MonimeService monimeService;

    @Value("${site.base-url}")
    private String siteBaseUrl;

    public SupportController(ContactService contactService, MonimeService monimeService) {
        this.contactService = contactService;
        this.monimeService = monimeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckout(@RequestBody Map<String, Object> body,
                                            HttpServletRequest request) {
        try {
            int amount = Integer.parseInt(String.valueOf(body.get("amount")));

            String ip = WebUtils.getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            contactService.saveSupportClick(ip, userAgent != null ? userAgent : "");

            String redirectUrl = monimeService.createCheckoutSession(amount);
            return ResponseEntity.ok(Map.of("url", redirectUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong creating the payment. Please try again."));
        }
    }

    @RequestMapping(value = "/success", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Void> paymentSuccess() {
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create(siteBaseUrl + "/thank-you.html"))
                .build();
    }
}