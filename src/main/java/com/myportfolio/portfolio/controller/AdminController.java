package com.myportfolio.portfolio.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/verify")
    public Map<String, Object> verify() {
        return Map.of("success", true, "message", "Login verified.");
    }
}