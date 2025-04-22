package com.product.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CategoryRequest {

    private Long categoryId;
    private String categoryType;
    private Date createdDate;
    private Date updateDate;
}
