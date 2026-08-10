package com.myportfolio.portfolio.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class JobPosting {

    @DocumentId
    private String id;

    private String title;
    private String purpose;
    private int order;

    @PropertyName("is_active")
    private boolean isActive;

    private List<CustomField> fields;

    @Data
    @NoArgsConstructor
    public static class CustomField {
        private String label;
        private String type;
        private boolean required;
        private int order;
    }
}