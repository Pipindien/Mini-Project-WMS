package com.product.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CategoryResponse {
    private String categoryType;
    private Long categoryId;
    private Date createdDate;
    private Date updateDate;
}
