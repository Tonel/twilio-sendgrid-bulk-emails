package com.twilio.bulkemails.controllers;

import com.twilio.bulkemails.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emails")
public class EmailController {
    private final EmailService emailService;

    public EmailController(
            @Autowired EmailService emailService
    ) {
        this.emailService = emailService;
    }

    @PostMapping("/sendSingleEmail")
    public ResponseEntity<String> sendSingleEmail(
            @RequestBody String to
    ) {
        // send single email
        emailService.sendSingleEmail(to);

        return ResponseEntity.ok("Single email sent successfully!");
    }

    @PostMapping("/sendMultipleEmails")
    public ResponseEntity<String> sendMultipleEmails(
            @RequestBody List<String> tos
    ) {
        // send the same email to multiple recipients
        emailService.sendMultipleEmails(tos);

        return ResponseEntity.ok("Multiple emails sent successfully!");
    }

    @PostMapping("/sendBulkEmails")
    public ResponseEntity<String> sendBulkEmails(
            @RequestBody List<String> tos
    ) {
        // send bulk emails
        emailService.sendBulkEmails(tos);

        return ResponseEntity.ok("Bulk emails sent successfully!");
    }
}
