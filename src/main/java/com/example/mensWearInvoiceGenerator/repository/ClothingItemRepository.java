package com.example.mensWearInvoiceGenerator.repository;

import com.example.mensWearInvoiceGenerator.model.ClothingItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClothingItemRepository extends JpaRepository<ClothingItems,Long> {
    Optional<ClothingItems> findByTagId(String tagId);
}
