package com.product.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CategoryRequest {

    @NotNull
    private Long categoryId;
    @NotBlank
    private String categoryType;
    private Date createdDate;
    private Date updateDate;
}
