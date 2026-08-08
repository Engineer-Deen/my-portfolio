package com.myportfolio.portfolio.model;

import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ContactMessage {

    @NotBlank
    @Size(min = 2, max = 150)
    private String name;

    @NotBlank
    @Email
    private String email;

    @Size(max = 250)
    private String subject;

    @NotBlank
    @Size(min = 10)
    private String message;

    @PropertyName("ip_address")
    private String ipAddress;

    @PropertyName("is_read")
    private boolean isRead;

    @PropertyName("created_at")
    private Object createdAt;
}