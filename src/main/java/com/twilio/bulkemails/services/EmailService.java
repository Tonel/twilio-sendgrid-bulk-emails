package com.twilio.bulkemails.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import java.io.IOException;
import java.util.List;
import com.sendgrid.helpers.mail.objects.Personalization;

@Service
public class EmailService {
    private final SendGrid sendGrid;
    private final String fromEmail;

    public EmailService(
            // get the SendGrid bean automatically created by Spring Boot
            @Autowired SendGrid sendGrid,
            // read your email to use as sender from application.properties
            @Value("${twilio.sendgrid.from-email}") String fromEmail
    ) {
        this.sendGrid = sendGrid;
        this.fromEmail = fromEmail;
    }

    public void sendSingleEmail(String toEmail) {
        // specify the email details
        Email from = new Email(this.fromEmail);
        String subject = "Hello, World!";
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", "Welcome to the Twilio SendGrid world!");

        // initialize the Mail helper class
        Mail mail = new Mail(from, subject, to, content);

        // send the single email
        sendEmail(mail);
    }

    public void sendMultipleEmails(List<String> tos) {
        // iterate over the list of destination emails
        // and email each of them
        tos.parallelStream().forEach(this::sendSingleEmail);
    }

    public void sendBulkEmails(List<String> tos) {
        // specify the email details
        Mail mail = new Mail();
        mail.setFrom(new Email(this.fromEmail));
        mail.setSubject("[BULK] Hello, World!");
        mail.addContent(new Content("text/html", "Welcome to the Twilio SendGrid world where you can send <strong>bulk emails</strong>!"));

        // add the multiple recipients to the email
        Personalization personalization = new Personalization();
        tos.forEach(to -> {
            // add each destination email address to the BCC
            // field of the email
            personalization.addBcc(new Email(to));
        });
        mail.addPersonalization(personalization);

        // send the bulk email
        sendEmail(mail);
    }

    private void sendEmail(Mail mail) {
        try {
            // set the SendGrid API endpoint details as described
            // in the doc (https://docs.sendgrid.com/api-reference/mail-send/mail-send)
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            // perform the request and send the email
            Response response = sendGrid.api(request);
            int statusCode = response.getStatusCode();
            // if the status code is not 2xx
            if (statusCode < 200 || statusCode >= 300) {
                throw new RuntimeException(response.getBody());
            }
        } catch (IOException e) {
            // log the error message in case of network failures
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
