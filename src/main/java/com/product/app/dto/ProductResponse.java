package com.product.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductResponse {
    private Long productId;
    private String produkSpecific;
    private String productName;
    private Integer productValue;
    private Long categoryId;
    private Date createdDate;
}
