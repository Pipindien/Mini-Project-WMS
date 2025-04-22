package com.product.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.advice.exception.CategoryNotFoundException;
import com.product.app.advice.exception.ProductNotFoundException;
import com.product.app.dto.CategoryRequest;
import com.product.app.dto.CategoryResponse;
import com.product.app.dto.ProductResponse;
import com.product.app.entity.Category;
import com.product.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @PostMapping("/save")
    public ResponseEntity<CategoryResponse> saveCategory(@RequestBody CategoryRequest categoryRequest) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(categoryRequest));
    }

    @GetMapping("/{categoryType}")
    public ResponseEntity<CategoryResponse> getCategoryType(@PathVariable String categoryType) throws JsonProcessingException {
        return ResponseEntity.ok(categoryService.getCategoryByCategory(categoryType));
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryRequest categoryRequest
    ) throws JsonProcessingException {
        CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) throws JsonProcessingException {
        String message = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(message);
    }

    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategory();
            return ResponseEntity.ok(categories);
        } catch (CategoryNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        } catch (JsonProcessingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error while processing JSON."));
        }
    }

}
