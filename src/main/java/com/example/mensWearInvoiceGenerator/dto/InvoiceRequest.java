package com.example.mensWearInvoiceGenerator.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceRequest {
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private List<InvoiceItemRequest> items;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;
}

