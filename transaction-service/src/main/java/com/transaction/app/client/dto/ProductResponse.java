package com.transaction.app.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long productId;
    private Long categoryId;
    private String productName;
    private Double productPrice;
    private Double productRate;
}
