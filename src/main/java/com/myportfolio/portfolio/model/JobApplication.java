package com.myportfolio.portfolio.model;

import com.google.cloud.firestore.annotation.PropertyName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class JobApplication {

    private String postingId;
    private String postingTitle;

    @NotBlank(message = "Please enter your name.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String applicantName;

    @NotBlank(message = "Please enter your email.")
    @Email(message = "Please enter a valid email address.")
    private String applicantEmail;

    private Map<String, String> responses;

    @PropertyName("cv_file_name")
    private String cvFileName;

    @PropertyName("cv_url")
    private String cvUrl;

    @PropertyName("ip_address")
    private String ipAddress;

    @PropertyName("created_at")
    private Object createdAt;
}