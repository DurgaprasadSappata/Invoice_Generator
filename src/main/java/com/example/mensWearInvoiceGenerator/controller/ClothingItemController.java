package com.example.mensWearInvoiceGenerator.controller;

import com.example.mensWearInvoiceGenerator.dto.InvoiceCalculationResult;
import com.example.mensWearInvoiceGenerator.dto.InvoiceItemRequest;
import com.example.mensWearInvoiceGenerator.model.ClothingItems;
import com.example.mensWearInvoiceGenerator.service.ClothingItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ClothingItemController {

    @Autowired
    private ClothingItemService itemService;

    @PostMapping("/addItem")
    public ClothingItems addItems(@RequestBody ClothingItems item){
        return itemService.addItem(item);
    }

    @GetMapping
    public List<ClothingItems> findAllItems(){
       return itemService.getAllItems();
    }

    @GetMapping("/{tagId}")
    public ClothingItems getItemByTagId(@PathVariable String tagId){
        return itemService.findItemByTagId(tagId);
    }

    @PostMapping("/invoice/calculate")
    public InvoiceCalculationResult calculateTotalInvoice(@RequestBody List< InvoiceItemRequest> items){
        return itemService.calculateInvoiceTotals(items);
    }

    @DeleteMapping("/{tagId}")
    public void removeItem(@PathVariable String tagId){
        itemService.removeItem(tagId);
    }
}
