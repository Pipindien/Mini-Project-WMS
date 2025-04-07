package com.product.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {

    private String type;
}
