package com.example.mensWearInvoiceGenerator.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, byte[] pdfData) throws MessagingException {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message,true);

                helper.setFrom("durgaprasad6816@gmail.com");
                helper.setTo(toEmail);
                helper.setSubject("Tax Invoice");
                helper.setText("Dear customer,\n\nPlease find attached your tax invoice.\n\nThank you!");


        ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfData, "application/pdf");
                helper.addAttachment("InvoiceBill.pdf",dataSource);
                mailSender.send(message);
    }
}

