package com.myportfolio.portfolio.service;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.myportfolio.portfolio.model.JobApplication;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ApplicationService {

    private final Firestore firestore;
    private final EmailService emailService;

    public ApplicationService(Firestore firestore, EmailService emailService) {
        this.firestore = firestore;
        this.emailService = emailService;
    }

    public void saveApplication(JobApplication application, String ipAddress)
            throws ExecutionException, InterruptedException {

        application.setIpAddress(ipAddress);
        application.setCreatedAt(Timestamp.now());

        firestore.collection("job_applications").add(application).get();

        try {
            emailService.sendApplicationNotification(application);
        } catch (Exception e) {
            System.err.println("Failed to send application notification email: " + e.getMessage());
        }
    }
}