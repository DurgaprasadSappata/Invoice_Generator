package com.example.mensWearInvoiceGenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemRequest {
    private String tagId;
    private int quantity;
    private double discount;
    private double cgstRate;
    private double sgstRate;
    private double igstRate;
}
