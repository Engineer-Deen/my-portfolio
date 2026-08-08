package com.myportfolio.portfolio.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class Project {

    @DocumentId
    private String id;
    private String title;
    private String category;

    private String description;
    private String purpose;
    private String technologies;

    @PropertyName("github_url")
    private String githubUrl;

    @PropertyName("live_url")
    private String liveUrl;

    private int order;

    @PropertyName("is_active")
    private boolean isActive;

    @PropertyName("created_at")
    private Object createdAt; // Firestore Timestamp; kept generic for now

    // Not stored in Firestore — computed when we send the JSON response
    public String getCategoryDisplay() {
        return switch (category) {
            case "cyber" -> "Cybersecurity";
            case "backend" -> "Backend";
            case "db" -> "Database";
            default -> category;
        };
    }

    public List<String> getTechList() {
        if (technologies == null || technologies.isBlank()) return List.of();
        return Arrays.stream(technologies.split(","))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .toList();
    }
}