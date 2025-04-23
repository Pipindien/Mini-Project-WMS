package com.product.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.app.advice.exception.CategoryNotFoundException;
import com.product.app.constant.GeneralConstant;
import com.product.app.dto.CategoryRequest;
import com.product.app.dto.CategoryResponse;
import com.product.app.entity.Category;
import com.product.app.repository.CategoryRepository;
import com.product.app.service.AuditTrailsService;
import com.product.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplementation implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AuditTrailsService auditTrailsService;
    @Autowired
    private ObjectMapper mapper;

    private CategoryResponse convertToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryType(category.getCategoryType())
                .createdDate(category.getCreatedDate())
                .build();
    }

    public CategoryResponse saveCategory(CategoryRequest categoryRequest) throws JsonProcessingException {
        Category category = Category.builder()
                .categoryType(categoryRequest.getCategoryType())
                .createdDate(new Date())
                .build();
        Category savedCategory = categoryRepository.save(category);

        CategoryResponse response = convertToCategoryResponse(savedCategory);

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACTIVITY_SAVE_CATEGORY,
                mapper.writeValueAsString(categoryRequest), mapper.writeValueAsString(response),
                "Insert Category Success"
        );
        return response;
    }

    public CategoryResponse getCategoryByCategory(String categoryType) throws JsonProcessingException {
        try {
            Optional<Category> category = categoryRepository.findCategoryByCategoryType(categoryType);
            if (category.isEmpty()) {
                throw new CategoryNotFoundException("Category Not Found");
            }

            CategoryResponse categoryResponse = convertToCategoryResponse(category.get());

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
        try {
            Category existingCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

            CategoryResponse oldData = convertToCategoryResponse(existingCategory);

            existingCategory.setCategoryType(categoryRequest.getCategoryType());
            existingCategory.setUpdateDate(categoryRequest.getUpdateDate());
            Category updatedCategory = categoryRepository.save(existingCategory);

            CategoryResponse response = convertToCategoryResponse(updatedCategory);

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
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

            CategoryResponse oldData = convertToCategoryResponse(category);

            category.setDeleted(true);
            categoryRepository.save(category);

            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_DELETE_CATEGORY,
                    mapper.writeValueAsString(oldData), "", "Soft Delete Category Success");

            return "Kategori berhasil dihapus.";
        } catch (CategoryNotFoundException ex) {
            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_DELETE_CATEGORY,
                    mapper.writeValueAsString(categoryId), mapper.writeValueAsString(ex.getMessage()),
                    "Failed Delete Category");
            throw ex;
        }
    }

    public List<CategoryResponse> getAllCategory() throws JsonProcessingException {
        List<Category> categories = categoryRepository.findAll();

        if (categories == null || categories.isEmpty()) {
            String message = "No Category Found";
            auditTrailsService.logsAuditTrails(
                    GeneralConstant.LOG_ACVITIY_GET_ALL_CATEGORY,
                    mapper.writeValueAsString("Get All Categories"),
                    mapper.writeValueAsString(message),
                    "Failed to Get Categories"
            );
            throw new CategoryNotFoundException(message);
        }

        List<CategoryResponse> categoryResponses = categories.stream()
                .map(this::convertToCategoryResponse)
                .collect(Collectors.toList());

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GET_ALL_CATEGORY,
                mapper.writeValueAsString("Get All Categories"),
                mapper.writeValueAsString(categoryResponses),
                "Successfully Retrieved All Categories"
        );

        return categoryResponses;
    }
}
