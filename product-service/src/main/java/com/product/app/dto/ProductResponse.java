package com.product.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductResponse {
    private Long productId;
    private String productName;
    private Double productPrice;
    private Double productRate;
    private Long categoryId;
    private String productCategory;
    private Date createdDate;
}
