package com.myportfolio.portfolio.controller;

import com.myportfolio.portfolio.model.JobApplication;
import com.myportfolio.portfolio.service.ApplicationService;
import com.myportfolio.portfolio.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<?> submitApplication(@RequestBody JobApplication application,
                                               HttpServletRequest request)
            throws ExecutionException, InterruptedException {

        String ip = WebUtils.getClientIp(request);
        applicationService.saveApplication(application, ip);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "success", true,
                        "message", "Application received. Thank you for applying!"
                ));
    }
}