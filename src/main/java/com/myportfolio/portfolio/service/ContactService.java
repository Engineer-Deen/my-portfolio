package com.myportfolio.portfolio.service;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.myportfolio.portfolio.model.ContactMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class
ContactService {

    private final Firestore firestore;
    private final EmailService emailService;

    public ContactService(Firestore firestore, EmailService emailService) {
        this.firestore = firestore;
        this.emailService = emailService;
    }

    public void saveContactMessage(ContactMessage message, String ipAddress)
            throws ExecutionException, InterruptedException {

        message.setIpAddress(ipAddress);
        message.setRead(false);
        message.setCreatedAt(Timestamp.now());

        firestore.collection("contact_messages").add(message).get();

        // Email failure should NOT prevent the success response —
        // same fail_silently=True behavior as the Django version
        try {
            emailService.sendContactNotification(message);
        } catch (Exception e) {
            System.err.println("Failed to send contact notification email: " + e.getMessage());
        }
    }

    public void saveSupportClick(String ipAddress, String userAgent)
            throws ExecutionException, InterruptedException {

        Map<String, Object> payload = new HashMap<>();
        payload.put("ip_address", ipAddress);
        payload.put("user_agent", userAgent);
        payload.put("clicked_at", Timestamp.now());

        firestore.collection("support_clicks").add(payload).get();
    }
}