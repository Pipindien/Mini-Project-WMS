package com.transaction.app.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private Long categoryId;
    private String productName;
    private Double productPrice;
    private Double productRate;
    private String productCategory;
}
