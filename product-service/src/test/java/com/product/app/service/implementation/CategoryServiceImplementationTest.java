package com.product.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.app.advice.exception.CategoryNotFoundException;
import com.product.app.client.dto.AuditTrailsRequest;
import com.product.app.constant.GeneralConstant;
import com.product.app.dto.CategoryRequest;
import com.product.app.dto.CategoryResponse;
import com.product.app.entity.Category;
import com.product.app.repository.CategoryRepository;
import com.product.app.service.AuditTrailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplementationTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuditTrailsService auditTrailsService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CategoryServiceImplementation categoryService;

    private CategoryRequest categoryRequest;
    private Category category;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        categoryRequest = CategoryRequest.builder()
                .categoryType("Fruit")
                .build();

        Category category = Category.builder()
                .categoryId(1L)
                .categoryType("Fruit")
                .createdDate(new Date())
                .updateDate(new Date())
                .build();


        lenient().when(categoryRepository.save(any(Category.class))).thenReturn(category);
        lenient().when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        lenient().when(categoryRepository.findCategoryByCategoryType(any(String.class))).thenReturn(Optional.of(category));

        // Mock ObjectMapper to avoid NPE
        when(objectMapper.writeValueAsString(any())).thenReturn("mocked");

        // Mock auditTrailsService
        when(auditTrailsService.logsAuditTrails(any(), any(), any(), any()))
                .thenReturn(new AuditTrailsRequest()); // Sesuaikan dengan tipe kembalian yang sesuai
    }

    @Test
    void saveCategory() throws JsonProcessingException {
        CategoryResponse response = categoryService.saveCategory(categoryRequest);

        assertNotNull(response);
        assertEquals("Fruit", response.getCategoryType());

        verify(auditTrailsService).logsAuditTrails(eq(GeneralConstant.LOG_ACTIVITY_SAVE_CATEGORY), any(), any(), eq("Insert Category Success"));
    }

    @Test
    void getCategoryByCategory() throws JsonProcessingException {
        String categoryType = "Fruit";
        CategoryResponse response = categoryService.getCategoryByCategory(categoryType);

        assertNotNull(response);
        assertEquals(categoryType, response.getCategoryType());

        verify(auditTrailsService).logsAuditTrails(eq(GeneralConstant.LOG_ACVITIY_GET_CATEGORY_TYPE), any(), any(), eq("Get Category Type Success"));
    }

    @Test
    void updateCategory() throws JsonProcessingException {
        Long categoryId = 1L;
        CategoryRequest updatedCategoryRequest = CategoryRequest.builder().categoryType("Vegetable").build();

        CategoryResponse response = categoryService.updateCategory(categoryId, updatedCategoryRequest);

        assertNotNull(response);
        assertEquals("Vegetable", response.getCategoryType());

        verify(auditTrailsService).logsAuditTrails(eq(GeneralConstant.LOG_ACVITIY_UPDATE_CATEGORY), any(), any(), eq("Update Category Success"));
    }

    @Test
    void deleteCategory() throws JsonProcessingException {
        Long categoryId = 1L;

        String result = categoryService.deleteCategory(categoryId);

        assertEquals("Kategori berhasil dihapus.", result);

        verify(auditTrailsService).logsAuditTrails(eq(GeneralConstant.LOG_ACVITIY_DELETE_CATEGORY), any(), eq(""), eq("Delete Category Success"));
    }


    @Test
    void getAllCategoryNotFound() throws JsonProcessingException {
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getAllCategory();
        });

        assertEquals("No Category Found", exception.getMessage());

        verify(auditTrailsService).logsAuditTrails(eq(GeneralConstant.LOG_ACVITIY_GET_ALL_CATEGORY), any(), any(), eq("Failed to Get Categories"));
    }
}
