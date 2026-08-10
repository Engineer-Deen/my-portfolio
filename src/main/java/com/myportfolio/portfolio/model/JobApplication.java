package com.myportfolio.portfolio.model;

import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class JobApplication {

    private String postingId;
    private String postingTitle;
    private String applicantName;
    private String applicantEmail;
    private Map<String, String> responses;

    @PropertyName("ip_address")
    private String ipAddress;

    @PropertyName("created_at")
    private Object createdAt;
}