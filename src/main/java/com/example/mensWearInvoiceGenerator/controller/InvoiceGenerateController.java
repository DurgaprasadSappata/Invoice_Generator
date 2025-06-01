package com.example.mensWearInvoiceGenerator.controller;

import com.example.mensWearInvoiceGenerator.dto.InvoiceRequest;
import com.example.mensWearInvoiceGenerator.service.EmailService;
import com.example.mensWearInvoiceGenerator.service.GenerateInvoicePdfService;
import com.itextpdf.io.exceptions.IOException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.Objects;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceGenerateController {

    @Autowired
    private GenerateInvoicePdfService service;

    @Autowired
    private EmailService emailService;

    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateInvoicePdf(@RequestBody InvoiceRequest invoiceRequest) throws IOException, MalformedURLException, MessagingException {
        byte[] invoicePdf = service.generateInvoicePdf(invoiceRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment","invoice.pdf");

        if(!(invoiceRequest.getCustomerEmail().isEmpty())){
            emailService.sendEmail(invoiceRequest.getCustomerEmail(),invoicePdf);
        }

        return new ResponseEntity<>(invoicePdf,headers, HttpStatus.OK);
    }
}
