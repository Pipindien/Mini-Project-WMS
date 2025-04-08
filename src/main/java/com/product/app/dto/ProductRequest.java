package com.product.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductRequest {
    private String productName;
    private String produkSpecific;
    private Integer productValue;
    private Long categoryId;
    private String productCategory;
    private Date createdDate;
}
