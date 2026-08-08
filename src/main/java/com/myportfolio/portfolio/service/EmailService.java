package com.myportfolio.portfolio.service;

import com.myportfolio.portfolio.model.ContactMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${contact.receiver-email}")
    private String receiverEmail;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendContactNotification(ContactMessage msg) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setReplyTo(msg.getEmail());
        email.setTo(receiverEmail);
        email.setSubject("📬 New message from " + msg.getName() +
                (msg.getSubject() == null || msg.getSubject().isBlank() ? "" : ": " + msg.getSubject()));

        String subjectLine = (msg.getSubject() == null || msg.getSubject().isBlank())
                ? "(no subject provided)"
                : msg.getSubject();

        email.setText(
                "You've received a new message through your portfolio site.\n" +
                        "─────────────────────────────────────────\n\n" +
                        "From:     " + msg.getName() + "\n" +
                        "Email:    " + msg.getEmail() + "\n" +
                        "Subject:  " + subjectLine + "\n\n" +
                        "Message:\n" + msg.getMessage() + "\n\n" +
                        "─────────────────────────────────────────\n" +
                        "Sent from IP: " + msg.getIpAddress() + "\n" +
                        "Reply directly to this email to respond to " + msg.getName() + "."
        );

        mailSender.send(email);
    }
}