package com.product.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductRequest {
    @NotBlank
    private String productName;
    @NotNull
    private Double productPrice;
    @NotNull
    private Double productRate;
    @NotNull
    private Long categoryId;
    private Date createdDate;
}
