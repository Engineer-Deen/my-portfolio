package com.myportfolio.portfolio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myportfolio.portfolio.model.ContactMessage;
import com.myportfolio.portfolio.model.JobApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${contact.receiver-email}")
    private String receiverEmail;

    public void sendContactNotification(ContactMessage msg) throws Exception {
        String subjectLine = (msg.getSubject() == null || msg.getSubject().isBlank())
                ? "(no subject provided)"
                : msg.getSubject();

        String body =
                "You've received a new message through your portfolio site.\n" +
                        "─────────────────────────────────────────\n\n" +
                        "From:     " + msg.getName() + "\n" +
                        "Email:    " + msg.getEmail() + "\n" +
                        "Subject:  " + subjectLine + "\n\n" +
                        "Message:\n" + msg.getMessage() + "\n\n" +
                        "─────────────────────────────────────────\n" +
                        "Sent from IP: " + msg.getIpAddress() + "\n" +
                        "Reply directly to this email to respond to " + msg.getName() + ".";

        Map<String, Object> payload = Map.of(
                "from", msg.getName() + " (via Portfolio) <" + fromEmail + ">",
                "to", List.of(receiverEmail),
                "reply_to", msg.getEmail(),
                "subject", "📬 New message from " + msg.getName() +
                        (subjectLine.equals("(no subject provided)") ? "" : ": " + subjectLine),
                "text", body
        );

        sendViaResend(payload);
    }

    public void sendApplicationNotification(JobApplication application) throws Exception {
        StringBuilder responsesText = new StringBuilder();
        if (application.getResponses() != null) {
            application.getResponses().forEach((label, value) ->
                    responsesText.append(label).append(": ").append(value).append("\n")
            );
        }

        String body =
                "New application received for: " + application.getPostingTitle() + "\n" +
                        "─────────────────────────────────────────\n\n" +
                        "Applicant Name:  " + application.getApplicantName() + "\n" +
                        "Applicant Email: " + application.getApplicantEmail() + "\n\n" +
                        responsesText +
                        "\n─────────────────────────────────────────\n" +
                        "Sent from IP: " + application.getIpAddress() + "\n" +
                        "Reply directly to this email to respond to " + application.getApplicantName() + ".";

        Map<String, Object> payload = Map.of(
                "from", application.getApplicantName() + " (Job Application) <" + fromEmail + ">",
                "to", List.of(receiverEmail),
                "reply_to", application.getApplicantEmail(),
                "subject", "📋 New application: " + application.getPostingTitle() + " — " + application.getApplicantName(),
                "text", body
        );

        sendViaResend(payload);
    }

    private void sendViaResend(Map<String, Object> payload) throws Exception {
        String jsonPayload = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Resend API error (" + response.statusCode() + "): " + response.body());
        }
    }
}