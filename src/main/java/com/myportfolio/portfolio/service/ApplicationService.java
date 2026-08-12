package com.myportfolio.portfolio.service;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.myportfolio.portfolio.model.JobApplication;
import com.myportfolio.portfolio.model.JobPosting;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ApplicationService {

    private static final long MAX_CV_BYTES = 2_000_000; // 2MB

    private final Firestore firestore;
    private final EmailService emailService;

    public ApplicationService(Firestore firestore, EmailService emailService) {
        this.firestore = firestore;
        this.emailService = emailService;
    }

    public void saveApplication(JobApplication application, MultipartFile cv, String ipAddress)
            throws IOException, ExecutionException, InterruptedException {

        validateAgainstPosting(application);

        if (cv == null || cv.isEmpty()) {
            throw new IllegalArgumentException("Please attach your CV/Resume.");
        }

        if (cv.getSize() > MAX_CV_BYTES) {
            throw new IllegalArgumentException("CV file is too large. Please use a file under 2MB.");
        }

        application.setIpAddress(ipAddress);
        application.setCreatedAt(Timestamp.now());
        application.setCvFileName(cv.getOriginalFilename());

        firestore.collection("job_applications").add(application).get();

        try {
            emailService.sendApplicationNotification(application, cv.getBytes(), cv.getOriginalFilename());
        } catch (Exception e) {
            System.err.println("Failed to send application notification email: " + e.getMessage());
        }
    }

    private void validateAgainstPosting(JobApplication application)
            throws ExecutionException, InterruptedException {

        DocumentSnapshot doc = firestore.collection("job_postings")
                .document(application.getPostingId())
                .get()
                .get();

        if (!doc.exists()) {
            throw new IllegalArgumentException("This position is no longer available.");
        }

        JobPosting posting = doc.toObject(JobPosting.class);
        if (posting == null || !posting.isActive()) {
            throw new IllegalArgumentException("This position is no longer accepting applications.");
        }

        List<JobPosting.CustomField> fields = posting.getFields();
        if (fields == null) return;

        for (JobPosting.CustomField field : fields) {
            if (!field.isRequired()) continue;

            String value = application.getResponses() != null
                    ? application.getResponses().get(field.getLabel())
                    : null;

            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Please fill in: " + field.getLabel());
            }
        }
    }
}