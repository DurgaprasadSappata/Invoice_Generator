package com.example.mensWearInvoiceGenerator.service;

import com.example.mensWearInvoiceGenerator.dto.InvoiceCalculationResult;
import com.example.mensWearInvoiceGenerator.dto.InvoiceItemRequest;
import com.example.mensWearInvoiceGenerator.model.ClothingItems;
import com.example.mensWearInvoiceGenerator.repository.ClothingItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClothingItemService {

    @Autowired
    private ClothingItemRepository repository;

    public ClothingItems addItem(ClothingItems items){
        return repository.save(items);
    }

    public List<ClothingItems> getAllItems(){
        return repository.findAll();
    }

    public ClothingItems findItemByTagId(String tagId){
        return repository.findByTagId(tagId)
                .orElseThrow(()->new RuntimeException("Items not Found "+tagId));
    }

    public void removeItem(String tagId){
        ClothingItems item = findItemByTagId(tagId);
        repository.delete(item);
    }

    public InvoiceCalculationResult calculateInvoiceTotals(List<InvoiceItemRequest> items) {
        double totalBeforeTax = 0.0;
        double totalCgst = 0.0;
        double totalSgst = 0.0;
        double totalIgst = 0.0;
        int totalQuantity = 0;

        for (InvoiceItemRequest request : items) {
            ClothingItems item = repository.findByTagId(request.getTagId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + request.getTagId()));

            int qty = request.getQuantity();
            double price = item.getPrice();
            double gross = price * qty;
            double discount = gross * (request.getDiscount() / 100);
            double taxableAmount = gross - discount;

            double cgst = taxableAmount * (request.getCgstRate() / 100);
            double sgst = taxableAmount * (request.getSgstRate() / 100);
            double igst = taxableAmount * (request.getIgstRate() / 100);

            totalBeforeTax += taxableAmount;
            totalCgst += cgst;
            totalSgst += sgst;
            totalIgst += igst;
            totalQuantity += qty;
        }

        double totalTax = totalCgst + totalSgst + totalIgst;
        double totalAfterTax = totalBeforeTax + totalTax;

        return InvoiceCalculationResult.builder()
                .totalBeforeTax(round(totalBeforeTax))
                .totalCgst(round(totalCgst))
                .totalSgst(round(totalSgst))
                .totalIgst(round(totalIgst))
                .totalTax(round(totalTax))
                .totalAfterTax(round(totalAfterTax))
                .totalQuantity(totalQuantity)
                .build();
    }

    // Optional rounding helper
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
