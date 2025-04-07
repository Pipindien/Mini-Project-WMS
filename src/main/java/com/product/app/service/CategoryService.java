package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.app.constant.GeneralConstant;
import com.product.app.dto.CategoryRequest;
import com.product.app.dto.CategoryResponse;
import com.product.app.entity.Category;
import com.product.app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class CategoryService {

    @Autowired
    private  CategoryRepository categoryRepository;
    @Autowired
    private  AuditTrailsService auditTrailsService;
    @Autowired
    private  ObjectMapper mapper;

    public CategoryResponse saveCategory(CategoryRequest categoryRequest) throws JsonProcessingException {
        Category category = Category.builder()
                .type(categoryRequest.getType())
                .build();
        Category savedCategory = categoryRepository.save(category);

        CategoryResponse response = CategoryResponse.builder()
                .type(savedCategory.getType())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACTIVITY_SAVE_CATEGORY,
                mapper.writeValueAsString(categoryRequest), mapper.writeValueAsString(response),
                "Insert Category Success"
        );
        return response;
    }

    public CategoryResponse getCategoryByType(String type) throws JsonProcessingException {

        Optional<Category> category = categoryRepository.findCategoryByType(type);
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .type(category.get().getType())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_GET_CATEGORY_TYPE,
                mapper.writeValueAsString(""), mapper.writeValueAsString(categoryResponse),
                "Get Category Type Success");

        return categoryResponse;
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) throws JsonProcessingException {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));

        CategoryResponse oldData = CategoryResponse.builder()
                .type(existingCategory.getType())
                .build();

        // Update data kategori
        existingCategory.setType(categoryRequest.getType());
        Category updatedCategory = categoryRepository.save(existingCategory);

        CategoryResponse response = CategoryResponse.builder()
                .type(updatedCategory.getType())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_UPDATE_CATEGORY,
                mapper.writeValueAsString(oldData), mapper.writeValueAsString(response),
                "Update Category Success");

        return response;
    }

    public String deleteCategory(Long categoryId) throws JsonProcessingException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));

        CategoryResponse oldData = CategoryResponse.builder()
                .type(category.getType())
                .build();

        categoryRepository.delete(category);

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_DELETE_CATEGORY,
                mapper.writeValueAsString(oldData), "", "Delete Category Success");

        return "Kategori berhasil dihapus.";
    }



}
