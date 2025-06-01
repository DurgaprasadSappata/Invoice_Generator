package com.example.mensWearInvoiceGenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceCalculationResult {
    private double totalBeforeTax;
    private double totalCgst;
    private double totalSgst;
    private double totalIgst;
    private double totalTax;
    private double totalAfterTax;
    private int totalQuantity;
    private PaymentMode paymentMode;
}
