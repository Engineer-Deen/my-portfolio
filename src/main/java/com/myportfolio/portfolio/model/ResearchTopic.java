package com.myportfolio.portfolio.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResearchTopic {

    @DocumentId
    private String id;
    private String title;
    private String description;
    private int progress;

    @PropertyName("status_label")
    private String statusLabel;

    private int order;

    @PropertyName("is_active")
    private boolean isActive;

    public int getClampedProgress() {
        return Math.max(0, Math.min(100, progress));
    }
}