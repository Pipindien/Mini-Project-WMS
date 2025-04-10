package com.product.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    private String productName;
    private Double productValue;
    private Long categoryId;
    private Boolean isDeleted = false;
    private Date createdDate;

    @OneToMany(mappedBy = "product")
    private List<ProductValueHistory> productValueHistories;
}
