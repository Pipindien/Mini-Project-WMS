package com.transaction.app.client.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProductRequest {
    private Long productId;
    private String productName;
    private Double productPrice;
    private String productCategory;
    private Date createdDate;
}
