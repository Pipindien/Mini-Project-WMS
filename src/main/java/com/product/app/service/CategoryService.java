package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.app.advice.exception.CategoryNotFoundException;
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
                .categoryType(categoryRequest.getCategoryType())
                .build();
        Category savedCategory = categoryRepository.save(category);

        CategoryResponse response = CategoryResponse.builder()
                .categoryType(savedCategory.getCategoryType())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACTIVITY_SAVE_CATEGORY,
                mapper.writeValueAsString(categoryRequest), mapper.writeValueAsString(response),
                "Insert Category Success"
        );
        return response;
    }

    public CategoryResponse getCategoryByCategory(String categoryType) throws JsonProcessingException {
        try {
        Optional<Category> category = categoryRepository.findCategoryByCategoryType(categoryType);
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .categoryType(category.get().getCategoryType())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_GET_CATEGORY_TYPE,
                mapper.writeValueAsString(""), mapper.writeValueAsString(categoryResponse),
                "Get Category Type Success");

        return categoryResponse;
        } catch (CategoryNotFoundException ex) {
            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_GET_CATEGORY_TYPE,
                    mapper.writeValueAsString(categoryType), mapper.writeValueAsString(ex.getMessage()),
                    "Failed Get Category Type");
            throw ex;
        }
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) throws JsonProcessingException {
        try{
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        CategoryResponse oldData = CategoryResponse.builder()
                .categoryType(existingCategory.getCategoryType())
                .build();

        existingCategory.setCategoryType(categoryRequest.getCategoryType());
        Category updatedCategory = categoryRepository.save(existingCategory);

        CategoryResponse response = CategoryResponse.builder()
                .categoryType(updatedCategory.getCategoryType())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_UPDATE_CATEGORY,
                mapper.writeValueAsString(oldData), mapper.writeValueAsString(response),
                "Update Category Success");

        return response;
        } catch (CategoryNotFoundException ex) {
            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_UPDATE_CATEGORY,
                    mapper.writeValueAsString(categoryRequest), mapper.writeValueAsString(ex.getMessage()),
                    "Failed Update Category");
            throw ex;
        }
    }

    public String deleteCategory(Long categoryId) throws JsonProcessingException {
        try{
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        CategoryResponse oldData = CategoryResponse.builder()
                .categoryType(category.getCategoryType())
                .build();

        categoryRepository.delete(category);

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_DELETE_CATEGORY,
                mapper.writeValueAsString(oldData), "", "Delete Category Success");

        return "Kategori berhasil dihapus.";
        } catch (CategoryNotFoundException ex) {
            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_DELETE_CATEGORY,
                    mapper.writeValueAsString(categoryId), mapper.writeValueAsString(ex.getMessage()),
                    "Failed Delete Category");
            throw ex;
        }
    }
}
