package com.product.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductResponse {
    private Long productId;
    private String productSpecific;
    private String productName;
    private Double productValue;
    private Long categoryId;
    private Date createdDate;
}
