package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.CategoryRequest;
import com.product.app.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse saveCategory(CategoryRequest categoryRequest) throws JsonProcessingException;

    CategoryResponse getCategoryByCategory(String categoryType) throws JsonProcessingException;

    CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) throws JsonProcessingException;

    String deleteCategory(Long categoryId) throws JsonProcessingException;

    List<CategoryResponse> getAllCategory() throws JsonProcessingException;
}
