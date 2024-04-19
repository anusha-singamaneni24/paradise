package com.anusha.paradise.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
public class Product {
    @Id
    private UUID id;
    private String name;
    private String address;
    private String description;
    private String postalCode;
    private BigDecimal price;
    private String yearMade;
    private String productType;
    private List<String> features;
    private List<String> images;
    private String status;
    private String postedBy;
    private LocalDateTime postedOn;
    private LocalDateTime modifiedOn;
}
