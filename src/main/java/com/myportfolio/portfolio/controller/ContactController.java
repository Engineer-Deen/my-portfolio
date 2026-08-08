package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.model.ContactMessage;
import com.myportfolio.portfolio.service.ContactService;
import com.myportfolio.portfolio.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<?> submitContact(@Valid @RequestBody ContactMessage message,
                                           HttpServletRequest request)
            throws ExecutionException, InterruptedException {

        String ip = WebUtils.getClientIp(request);
        contactService.saveContactMessage(message, ip);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "success", true,
                        "message", "Message received. I'll be in touch soon."
                ));
    }
}